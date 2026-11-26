package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.PurchaseOrderBo;
import com.ruoyi.wms.domain.bo.PurchaseOrderDetailBo;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import com.ruoyi.wms.domain.entity.PurchaseOrderDetail;
import com.ruoyi.wms.domain.vo.PurchaseOrderDetailVo;
import com.ruoyi.wms.domain.vo.PurchaseOrderVo;
import com.ruoyi.wms.mapper.PurchaseOrderDetailMapper;
import com.ruoyi.wms.mapper.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 采购单 Service
 */
@RequiredArgsConstructor
@Service
public class PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final ReceiptOrderService receiptOrderService;

    public TableDataInfo<PurchaseOrderVo> queryPageList(PurchaseOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PurchaseOrder> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getOrderNo()), PurchaseOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOrderStatus() != null, PurchaseOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getWarehouseId() != null, PurchaseOrder::getWarehouseId, bo.getWarehouseId());
        lqw.orderByDesc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrderVo> page = purchaseOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    public PurchaseOrderVo queryById(Long id) {
        PurchaseOrderVo vo = purchaseOrderMapper.selectVoById(id);
        Assert.notNull(vo, "采购单不存在");
        vo.setDetails(purchaseOrderDetailMapper.queryDetailVoList(id));
        return vo;
    }

    public List<PurchaseOrderVo> queryList(PurchaseOrderBo bo) {
        LambdaQueryWrapper<PurchaseOrder> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getOrderNo()), PurchaseOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOrderStatus() != null, PurchaseOrder::getOrderStatus, bo.getOrderStatus());
        lqw.orderByDesc(PurchaseOrder::getCreateTime);
        return purchaseOrderMapper.selectVoList(lqw);
    }

    /**
     * 保存草稿
     */
    @Transactional
    public Long insertByBo(PurchaseOrderBo bo) {
        if (StrUtil.isBlank(bo.getOrderNo())) {
            bo.setOrderNo(generateOrderNo());
        }
        validateOrderNo(bo.getOrderNo(), null);
        PurchaseOrder order = MapstructUtils.convert(bo, PurchaseOrder.class);
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(ServiceConstants.PurchaseOrderStatus.DRAFT);
        }
        order.setTotalQuantity(sumPurchase(bo.getDetails()));
        purchaseOrderMapper.insert(order);
        saveDetails(order.getId(), bo.getDetails());
        if (!ServiceConstants.PurchaseOrderStatus.DRAFT.equals(order.getOrderStatus())) {
            receiptOrderService.syncPurchaseReceipt(order.getId());
        }
        return order.getId();
    }

    /**
     * 修改采购单（草稿可直接改；已生成入库单的同步更新入库单，已入库则禁止）
     */
    @Transactional
    public void updateByBo(PurchaseOrderBo bo) {
        PurchaseOrder db = purchaseOrderMapper.selectById(bo.getId());
        Assert.notNull(db, "采购单不存在");
        if (ServiceConstants.PurchaseOrderStatus.FINISHED.equals(db.getOrderStatus())
            || ServiceConstants.PurchaseOrderStatus.CLOSED.equals(db.getOrderStatus())) {
            throw new ServiceException("采购单已结束，无法修改", HttpStatus.CONFLICT, "采购单【" + db.getOrderNo() + "】已入库关闭");
        }
        if (!db.getOrderNo().equals(bo.getOrderNo())) {
            validateOrderNo(bo.getOrderNo(), bo.getId());
        }
        PurchaseOrder update = MapstructUtils.convert(bo, PurchaseOrder.class);
        update.setTotalQuantity(sumPurchase(bo.getDetails()));
        purchaseOrderMapper.updateById(update);
        // 重写明细（保留已入库/退货数量）
        rewriteDetails(bo.getId(), bo.getDetails());
        if (!ServiceConstants.PurchaseOrderStatus.DRAFT.equals(db.getOrderStatus())) {
            receiptOrderService.syncPurchaseReceipt(bo.getId());
        }
    }

    /**
     * 确认采购单：草稿 -> 执行中，同时生成采购入库单
     */
    @Transactional
    public void confirm(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        Assert.notNull(order, "采购单不存在");
        List<PurchaseOrderDetailVo> details = purchaseOrderDetailMapper.queryDetailVoList(id);
        if (CollUtil.isEmpty(details)) {
            throw new ServiceException("采购单明细不能为空", HttpStatus.CONFLICT, "请先添加采购明细");
        }
        purchaseOrderMapper.update(null, Wrappers.<PurchaseOrder>lambdaUpdate()
            .eq(PurchaseOrder::getId, id)
            .set(PurchaseOrder::getOrderStatus, ServiceConstants.PurchaseOrderStatus.RUNNING));
        receiptOrderService.syncPurchaseReceipt(id);
    }

    /**
     * 关闭采购单
     */
    public void close(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        Assert.notNull(order, "采购单不存在");
        purchaseOrderMapper.update(null, Wrappers.<PurchaseOrder>lambdaUpdate()
            .eq(PurchaseOrder::getId, id)
            .set(PurchaseOrder::getOrderStatus, ServiceConstants.PurchaseOrderStatus.CLOSED));
    }

    @Transactional
    public void deleteById(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        Assert.notNull(order, "采购单不存在");
        if (ServiceConstants.PurchaseOrderStatus.FINISHED.equals(order.getOrderStatus())) {
            throw new ServiceException("采购单已完成入库，无法删除", HttpStatus.CONFLICT, "采购单【" + order.getOrderNo() + "】");
        }
        purchaseOrderMapper.deleteById(id);
        purchaseOrderDetailMapper.delete(Wrappers.<PurchaseOrderDetail>lambdaQuery().eq(PurchaseOrderDetail::getOrderId, id));
        receiptOrderService.deletePurchaseReceipt(id);
    }

    @Transactional
    public void deleteByIds(Collection<Long> ids) {
        ids.forEach(this::deleteById);
    }

    /**
     * 导出用：展开的采购单明细
     */
    public List<PurchaseOrderDetailVo> exportList(PurchaseOrderBo bo) {
        List<PurchaseOrderVo> orders = queryList(bo);
        List<PurchaseOrderDetailVo> result = new ArrayList<>();
        for (PurchaseOrderVo order : orders) {
            List<PurchaseOrderDetailVo> details = purchaseOrderDetailMapper.queryDetailVoList(order.getId());
            result.addAll(details);
        }
        return result;
    }

    public PurchaseOrderVo queryByOrderNo(String orderNo) {
        PurchaseOrder order = purchaseOrderMapper.selectOne(Wrappers.<PurchaseOrder>lambdaQuery()
            .eq(PurchaseOrder::getOrderNo, orderNo));
        return order == null ? null : queryById(order.getId());
    }

    /**
     * 生成采购单号
     */
    public String generateOrderNo() {
        return "CG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + ThreadLocalRandom.current().nextInt(100, 999);
    }

    private void validateOrderNo(String orderNo, Long excludeId) {
        LambdaQueryWrapper<PurchaseOrder> lqw = Wrappers.<PurchaseOrder>lambdaQuery()
            .eq(PurchaseOrder::getOrderNo, orderNo)
            .ne(excludeId != null, PurchaseOrder::getId, excludeId);
        Assert.isTrue(!purchaseOrderMapper.exists(lqw), "采购单号重复");
    }

    private BigDecimal sumPurchase(List<PurchaseOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return BigDecimal.ZERO;
        }
        return details.stream()
            .map(d -> d.getPurchaseQuantity() == null ? BigDecimal.ZERO : d.getPurchaseQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void saveDetails(Long orderId, List<PurchaseOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return;
        }
        for (PurchaseOrderDetailBo detailBo : details) {
            PurchaseOrderDetail detail = MapstructUtils.convert(detailBo, PurchaseOrderDetail.class);
            detail.setId(null);
            detail.setOrderId(orderId);
            if (detail.getInboundQuantity() == null) {
                detail.setInboundQuantity(BigDecimal.ZERO);
            }
            if (detail.getReturnQuantity() == null) {
                detail.setReturnQuantity(BigDecimal.ZERO);
            }
            if (detail.getActualQuantity() == null) {
                detail.setActualQuantity(detail.getInboundQuantity().subtract(detail.getReturnQuantity()));
            }
            purchaseOrderDetailMapper.insert(detail);
        }
    }

    /**
     * 重写明细：按 skuId 匹配保留已入库/退货数量
     */
    private void rewriteDetails(Long orderId, List<PurchaseOrderDetailBo> details) {
        List<PurchaseOrderDetailVo> oldList = purchaseOrderDetailMapper.queryDetailVoList(orderId);
        java.util.Map<Long, PurchaseOrderDetailVo> oldMap = new java.util.HashMap<>();
        oldList.forEach(o -> oldMap.put(o.getSkuId(), o));
        purchaseOrderDetailMapper.delete(Wrappers.<PurchaseOrderDetail>lambdaQuery()
            .eq(PurchaseOrderDetail::getOrderId, orderId));
        if (CollUtil.isEmpty(details)) {
            return;
        }
        for (PurchaseOrderDetailBo detailBo : details) {
            PurchaseOrderDetail detail = MapstructUtils.convert(detailBo, PurchaseOrderDetail.class);
            detail.setId(null);
            detail.setOrderId(orderId);
            PurchaseOrderDetailVo old = oldMap.get(detailBo.getSkuId());
            detail.setInboundQuantity(old != null ? old.getInboundQuantity() : BigDecimal.ZERO);
            detail.setReturnQuantity(old != null ? old.getReturnQuantity() : BigDecimal.ZERO);
            detail.setActualQuantity(detail.getInboundQuantity().subtract(detail.getReturnQuantity()));
            purchaseOrderDetailMapper.insert(detail);
        }
    }
}
