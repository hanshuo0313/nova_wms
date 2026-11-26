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
import com.ruoyi.wms.domain.bo.ReturnOrderBo;
import com.ruoyi.wms.domain.bo.ReturnOrderDetailBo;
import com.ruoyi.wms.domain.bo.ShipmentOrderBo;
import com.ruoyi.wms.domain.bo.ShipmentOrderDetailBo;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import com.ruoyi.wms.domain.entity.PurchaseOrderDetail;
import com.ruoyi.wms.domain.entity.ReturnOrder;
import com.ruoyi.wms.domain.entity.ReturnOrderDetail;
import com.ruoyi.wms.domain.entity.ShipmentOrder;
import com.ruoyi.wms.domain.vo.PurchaseOrderDetailVo;
import com.ruoyi.wms.domain.vo.ReturnOrderDetailVo;
import com.ruoyi.wms.domain.vo.ReturnOrderVo;
import com.ruoyi.wms.mapper.PurchaseOrderDetailMapper;
import com.ruoyi.wms.mapper.PurchaseOrderMapper;
import com.ruoyi.wms.mapper.ReturnOrderDetailMapper;
import com.ruoyi.wms.mapper.ReturnOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 退货单 Service
 */
@RequiredArgsConstructor
@Service
public class ReturnOrderService {

    private final ReturnOrderMapper returnOrderMapper;
    private final ReturnOrderDetailMapper returnOrderDetailMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final InventoryService inventoryService;
    private final ItemSkuService itemSkuService;
    private final ShipmentOrderService shipmentOrderService;
    private final com.ruoyi.wms.mapper.ShipmentOrderMapper shipmentOrderMapper;

    public TableDataInfo<ReturnOrderVo> queryPageList(ReturnOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ReturnOrder> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getOrderNo()), ReturnOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOrderStatus() != null, ReturnOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getPurchaseOrderId() != null, ReturnOrder::getPurchaseOrderId, bo.getPurchaseOrderId());
        lqw.orderByDesc(ReturnOrder::getCreateTime);
        Page<ReturnOrderVo> page = returnOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    public ReturnOrderVo queryById(Long id) {
        ReturnOrderVo vo = returnOrderMapper.selectVoById(id);
        Assert.notNull(vo, "退货单不存在");
        java.util.List<com.ruoyi.wms.domain.vo.ReturnOrderDetailVo> detailList = returnOrderDetailMapper.queryDetailVoList(id);
        if (detailList != null) {
            detailList.forEach(detail -> {
                if (detail.getSkuId() != null) {
                    detail.setItemSku(itemSkuService.queryById(detail.getSkuId()));
                }
            });
        }
        vo.setDetails(detailList);
        return vo;
    }

    /**
     * 可退货明细：已完成采购单的明细，可退数量 = 实收数量 - 已退货数量
     */
    public List<ReturnOrderDetailVo> availableDetails(Long purchaseOrderId) {
        return availableDetails(purchaseOrderId, null);
    }

    /**
     * 可退货明细（excludeOrderId：编辑时排除当前退货单自身的已退数量）
     */
    public List<ReturnOrderDetailVo> availableDetails(Long purchaseOrderId, Long excludeOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectById(purchaseOrderId);
        Assert.notNull(purchaseOrder, "采购单不存在");
        List<PurchaseOrderDetail> poDetails = purchaseOrderDetailMapper.selectList(
            Wrappers.<PurchaseOrderDetail>lambdaQuery().eq(PurchaseOrderDetail::getOrderId, purchaseOrderId));
        List<PurchaseOrderDetailVo> poDetailVos = purchaseOrderDetailMapper.queryDetailVoList(purchaseOrderId);
        Map<Long, PurchaseOrderDetailVo> voMap = poDetailVos.stream()
            .collect(Collectors.toMap(PurchaseOrderDetailVo::getId, v -> v, (a, b) -> a));

        // 已退货数量（未作废的退货单，编辑时排除自身）
        LambdaQueryWrapper<ReturnOrder> orderLqw = Wrappers.<ReturnOrder>lambdaQuery()
            .eq(ReturnOrder::getPurchaseOrderId, purchaseOrderId)
            .ne(ReturnOrder::getOrderStatus, ServiceConstants.ReturnOrderStatus.INVALID)
            .ne(excludeOrderId != null, ReturnOrder::getId, excludeOrderId);
        List<ReturnOrder> returnOrders = returnOrderMapper.selectList(orderLqw);
        Map<Long, BigDecimal> returnedMap = new HashMap<>();
        for (ReturnOrder order : returnOrders) {
            List<ReturnOrderDetail> details = returnOrderDetailMapper.selectList(
                Wrappers.<ReturnOrderDetail>lambdaQuery().eq(ReturnOrderDetail::getOrderId, order.getId()));
            for (ReturnOrderDetail detail : details) {
                if (detail.getPurchaseDetailId() != null) {
                    returnedMap.merge(detail.getPurchaseDetailId(),
                        detail.getReturnQuantity() == null ? BigDecimal.ZERO : detail.getReturnQuantity(), BigDecimal::add);
                }
            }
        }

        List<ReturnOrderDetailVo> result = new ArrayList<>();
        for (PurchaseOrderDetail poDetail : poDetails) {
            PurchaseOrderDetailVo vo = voMap.get(poDetail.getId());
            BigDecimal actual = poDetail.getActualQuantity() == null ? BigDecimal.ZERO : poDetail.getActualQuantity();
            BigDecimal returned = returnedMap.getOrDefault(poDetail.getId(), BigDecimal.ZERO);
            BigDecimal available = actual.subtract(returned);
            if (available.signum() <= 0) {
                continue;
            }
            ReturnOrderDetailVo detailVo = new ReturnOrderDetailVo();
            detailVo.setPurchaseDetailId(poDetail.getId());
            detailVo.setSkuId(poDetail.getSkuId());
            if (vo != null) {
                detailVo.setItemId(vo.getItemId());
                detailVo.setItemCode(vo.getItemCode());
                detailVo.setItemName(vo.getItemName());
                detailVo.setSupplierId(vo.getSupplierId());
                detailVo.setSupplierName(vo.getSupplierName());
            }
            detailVo.setReceivedQuantity(actual);
            detailVo.setMaxQuantity(available);
            detailVo.setReturnQuantity(available);
            detailVo.setRemark("可退数量：" + available.stripTrailingZeros().toPlainString());
            result.add(detailVo);
        }
        return result;
    }

    @Transactional
    public Long insertByBo(ReturnOrderBo bo) {
        validateOrderNo(bo.getOrderNo(), null);
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectById(bo.getPurchaseOrderId());
        Assert.notNull(purchaseOrder, "关联采购单不存在");
        validateDetails(bo.getPurchaseOrderId(), bo.getDetails());

        ReturnOrder order = MapstructUtils.convert(bo, ReturnOrder.class);
        order.setOrderStatus(ServiceConstants.ReturnOrderStatus.PENDING);
        order.setPurchaseOrderNo(purchaseOrder.getOrderNo());
        order.setWarehouseId(purchaseOrder.getWarehouseId());
        order.setTotalQuantity(sum(bo.getDetails()));
        returnOrderMapper.insert(order);
        saveDetails(order.getId(), bo.getDetails());
        // 自动生成待出库的退货出库单，出库操作在出库单中完成
        createReturnShipment(order.getId());
        return order.getId();
    }

    @Transactional
    public void updateByBo(ReturnOrderBo bo) {
        ReturnOrder db = returnOrderMapper.selectById(bo.getId());
        Assert.notNull(db, "退货单不存在");
        if (ServiceConstants.ReturnOrderStatus.FINISH.equals(db.getOrderStatus())) {
            throw new ServiceException("退货单已完成，无法修改", HttpStatus.CONFLICT, "退货单【" + db.getOrderNo() + "】");
        }
        if (!db.getOrderNo().equals(bo.getOrderNo())) {
            validateOrderNo(bo.getOrderNo(), bo.getId());
        }
        validateDetails(db.getPurchaseOrderId(), bo.getDetails(), bo.getId());
        ReturnOrder update = MapstructUtils.convert(bo, ReturnOrder.class);
        update.setPurchaseOrderId(db.getPurchaseOrderId());
        update.setPurchaseOrderNo(db.getPurchaseOrderNo());
        update.setWarehouseId(db.getWarehouseId());
        update.setTotalQuantity(sum(bo.getDetails()));
        returnOrderMapper.updateById(update);
        returnOrderDetailMapper.delete(Wrappers.<ReturnOrderDetail>lambdaQuery()
            .eq(ReturnOrderDetail::getOrderId, bo.getId()));
        saveDetails(bo.getId(), bo.getDetails());
        // 同步未完成的退货出库单
        syncReturnShipment(bo.getId());
    }

    /**
     * 生成待执行的退货出库单（类型 3），出库操作在出库单中完成
     */
    private void createReturnShipment(Long returnOrderId) {
        ReturnOrderVo order = queryById(returnOrderId);
        ShipmentOrderBo shipmentBo = new ShipmentOrderBo();
        shipmentBo.setOrderNo("TH" + order.getOrderNo());
        shipmentBo.setOrderStatus(ServiceConstants.ShipmentOrderStatus.PENDING);
        shipmentBo.setOptType(4L);
        shipmentBo.setShipmentType(ServiceConstants.ShipmentType.RETURN);
        shipmentBo.setSourceOrderId(returnOrderId);
        shipmentBo.setSourceOrderNo(order.getOrderNo());
        shipmentBo.setWarehouseId(order.getWarehouseId());
        shipmentBo.setTotalQuantity(order.getTotalQuantity());
        shipmentBo.setRemark("由退货单【" + order.getOrderNo() + "】自动生成");
        List<ShipmentOrderDetailBo> shipmentDetails = new ArrayList<>();
        for (ReturnOrderDetailVo detail : order.getDetails()) {
            ShipmentOrderDetailBo detailBo = new ShipmentOrderDetailBo();
            detailBo.setSkuId(detail.getSkuId());
            detailBo.setQuantity(detail.getReturnQuantity());
            detailBo.setWarehouseId(order.getWarehouseId());
            detailBo.setStockCode(currentStockCode(order.getWarehouseId(), detail.getSkuId()));
            shipmentDetails.add(detailBo);
        }
        shipmentBo.setDetails(shipmentDetails);
        shipmentOrderService.insertByBo(shipmentBo);
    }

    /**
     * 同步退货出库单：删除未完成的旧单后重新生成
     */
    private void syncReturnShipment(Long returnOrderId) {
        List<ShipmentOrder> existing = shipmentOrderMapper.selectList(Wrappers.<ShipmentOrder>lambdaQuery()
            .eq(ShipmentOrder::getSourceOrderId, returnOrderId)
            .eq(ShipmentOrder::getShipmentType, ServiceConstants.ShipmentType.RETURN));
        for (ShipmentOrder shipmentOrder : existing) {
            if (!ServiceConstants.ShipmentOrderStatus.FINISH.equals(shipmentOrder.getOrderStatus())) {
                shipmentOrderService.deleteById(shipmentOrder.getId());
            }
        }
        createReturnShipment(returnOrderId);
    }

    /**
     * 退货出库单执行完成后调用：回写采购单退货数量、实收数量，退货单置为已完成
     */
    @Transactional
    public void finalizeByShipment(Long returnOrderId) {
        ReturnOrderVo order = queryById(returnOrderId);
        if (order == null || ServiceConstants.ReturnOrderStatus.FINISH.equals(order.getOrderStatus())) {
            return;
        }
        for (ReturnOrderDetailVo detail : order.getDetails()) {
            if (detail.getPurchaseDetailId() == null) {
                continue;
            }
            PurchaseOrderDetail poDetail = purchaseOrderDetailMapper.selectById(detail.getPurchaseDetailId());
            if (poDetail == null) {
                continue;
            }
            BigDecimal returned = poDetail.getReturnQuantity() == null ? BigDecimal.ZERO : poDetail.getReturnQuantity();
            returned = returned.add(detail.getReturnQuantity());
            poDetail.setReturnQuantity(returned);
            BigDecimal inbound = poDetail.getInboundQuantity() == null ? BigDecimal.ZERO : poDetail.getInboundQuantity();
            poDetail.setActualQuantity(inbound.subtract(returned));
            purchaseOrderDetailMapper.updateById(poDetail);
        }
        returnOrderMapper.update(null, Wrappers.<ReturnOrder>lambdaUpdate()
            .eq(ReturnOrder::getId, returnOrderId)
            .set(ReturnOrder::getOrderStatus, ServiceConstants.ReturnOrderStatus.FINISH));
    }

    @Transactional
    public void deleteById(Long id) {
        ReturnOrder order = returnOrderMapper.selectById(id);
        Assert.notNull(order, "退货单不存在");
        if (ServiceConstants.ReturnOrderStatus.FINISH.equals(order.getOrderStatus())) {
            throw new ServiceException("退货单已完成，无法删除", HttpStatus.CONFLICT, "退货单【" + order.getOrderNo() + "】");
        }
        returnOrderMapper.deleteById(id);
        returnOrderDetailMapper.delete(Wrappers.<ReturnOrderDetail>lambdaQuery()
            .eq(ReturnOrderDetail::getOrderId, id));
    }

    public String generateOrderNo() {
        return "TH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + ThreadLocalRandom.current().nextInt(100, 999);
    }

    private String currentStockCode(Long warehouseId, Long skuId) {
        List<Inventory> list = inventoryService.list(Wrappers.<Inventory>lambdaQuery()
            .eq(Inventory::getWarehouseId, warehouseId)
            .eq(Inventory::getSkuId, skuId));
        return list.isEmpty() ? null : list.get(0).getStockCode();
    }

    private void validateDetails(Long purchaseOrderId, List<ReturnOrderDetailBo> details) {
        validateDetails(purchaseOrderId, details, null);
    }

    private void validateDetails(Long purchaseOrderId, List<ReturnOrderDetailBo> details, Long excludeOrderId) {
        if (CollUtil.isEmpty(details)) {
            throw new ServiceException("退货明细不能为空", HttpStatus.CONFLICT, "请选择要退货的采购明细");
        }
        Map<Long, ReturnOrderDetailVo> available = availableDetails(purchaseOrderId, excludeOrderId).stream()
            .collect(Collectors.toMap(ReturnOrderDetailVo::getPurchaseDetailId, v -> v, (a, b) -> a));
        for (ReturnOrderDetailBo detail : details) {
            ReturnOrderDetailVo availableDetail = available.get(detail.getPurchaseDetailId());
            if (availableDetail == null) {
                throw new ServiceException("退货明细不在关联采购单中", HttpStatus.CONFLICT, "请重新选择退货明细");
            }
            if (detail.getReturnQuantity() == null || detail.getReturnQuantity().signum() <= 0) {
                throw new ServiceException("退货数量必须大于0", HttpStatus.CONFLICT, "物料：" + availableDetail.getItemName());
            }
            if (detail.getReturnQuantity().compareTo(availableDetail.getMaxQuantity()) > 0) {
                throw new ServiceException("退货数量不能大于实收数量", HttpStatus.CONFLICT,
                    "物料：" + availableDetail.getItemName() + " 可退数量：" + availableDetail.getMaxQuantity());
            }
        }
    }

    private void validateOrderNo(String orderNo, Long excludeId) {
        LambdaQueryWrapper<ReturnOrder> lqw = Wrappers.<ReturnOrder>lambdaQuery()
            .eq(ReturnOrder::getOrderNo, orderNo)
            .ne(excludeId != null, ReturnOrder::getId, excludeId);
        Assert.isTrue(!returnOrderMapper.exists(lqw), "退货单号重复");
    }

    private BigDecimal sum(List<ReturnOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return BigDecimal.ZERO;
        }
        return details.stream()
            .map(d -> d.getReturnQuantity() == null ? BigDecimal.ZERO : d.getReturnQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void saveDetails(Long orderId, List<ReturnOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return;
        }
        for (ReturnOrderDetailBo detailBo : details) {
            ReturnOrderDetail detail = MapstructUtils.convert(detailBo, ReturnOrderDetail.class);
            detail.setId(null);
            detail.setOrderId(orderId);
            returnOrderDetailMapper.insert(detail);
        }
    }
}
