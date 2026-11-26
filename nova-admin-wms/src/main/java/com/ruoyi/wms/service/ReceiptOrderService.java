package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.ReceiptOrderBo;
import com.ruoyi.wms.domain.bo.ReceiptOrderDetailBo;
import com.ruoyi.wms.domain.entity.ReceiptOrder;
import com.ruoyi.wms.domain.entity.ReceiptOrderDetail;
import com.ruoyi.wms.domain.vo.ReceiptOrderDetailVo;
import com.ruoyi.wms.domain.vo.ReceiptOrderVo;
import com.ruoyi.wms.mapper.ReceiptOrderMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import com.ruoyi.wms.mapper.ReceiptOrderDetailMapper;
import com.ruoyi.wms.mapper.PurchaseOrderDetailMapper;
import com.ruoyi.wms.mapper.PurchaseOrderMapper;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import com.ruoyi.wms.domain.entity.PurchaseOrderDetail;
import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.ruoyi.wms.mapper.InventoryLocationMapper;
import com.ruoyi.wms.domain.vo.ShipmentOrderDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 入库单Service业务层处理
 *
 * @author zcc
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class ReceiptOrderService {

    private final ReceiptOrderMapper receiptOrderMapper;
    private final InventoryLocationMapper inventoryLocationMapper;
    private final ReceiptOrderDetailMapper receiptOrderDetailMapper;
    private final ReceiptOrderDetailService receiptOrderDetailService;
    private final InventoryService inventoryService;
    private final InventoryHistoryService inventoryHistoryService;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final ShipmentOrderDetailService shipmentOrderDetailService;

    /**
     * 查询入库单
     */
    public ReceiptOrderVo queryById(Long id){
        ReceiptOrderVo receiptOrderVo = receiptOrderMapper.selectVoById(id);
        Assert.notNull(receiptOrderVo, "入库单不存在");
        receiptOrderVo.setDetails(receiptOrderDetailService.queryByReceiptOrderId(id));
        return receiptOrderVo;
    }

    public Long queryIdByOrderNo(String orderNo){
        ReceiptOrderVo receiptOrderVo = receiptOrderMapper.selectVoOne(new QueryWrapper<ReceiptOrder>().eq("order_no",orderNo));
        return receiptOrderVo != null ? receiptOrderVo.getId() : null;
    }

    /**
     * 查询入库单列表
     */
    public TableDataInfo<ReceiptOrderVo> queryPageList(ReceiptOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ReceiptOrder> lqw = buildQueryWrapper(bo);
        Page<ReceiptOrderVo> result = receiptOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询入库单列表
     */
    public List<ReceiptOrderVo> queryList(ReceiptOrderBo bo) {
        LambdaQueryWrapper<ReceiptOrder> lqw = buildQueryWrapper(bo);
        return receiptOrderMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ReceiptOrder> buildQueryWrapper(ReceiptOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ReceiptOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), ReceiptOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOptType() != null, ReceiptOrder::getOptType, bo.getOptType());
        lqw.eq(bo.getReceiptType() != null, ReceiptOrder::getReceiptType, bo.getReceiptType());
        lqw.eq(bo.getSourceOrderId() != null, ReceiptOrder::getSourceOrderId, bo.getSourceOrderId());
        lqw.eq(bo.getMerchantId() != null, ReceiptOrder::getMerchantId, bo.getMerchantId());
        lqw.eq(bo.getTotalAmount() != null, ReceiptOrder::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getOrderStatus() != null, ReceiptOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getWarehouseId() != null, ReceiptOrder::getWarehouseId, bo.getWarehouseId());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(CollUtil.isNotEmpty(warehouseScopeIds), ReceiptOrder::getWarehouseId, warehouseScopeIds);
        lqw.orderByDesc(BaseEntity::getCreateTime);
        return lqw;
    }

    /**
     * 暂存入库单
     */
    @Transactional
    public Long insertByBo(ReceiptOrderBo bo) {
        // 校验入库单号唯一性
        validateReceiptOrderNo(bo.getOrderNo());
        // 创建入库单
        ReceiptOrder add = MapstructUtils.convert(bo, ReceiptOrder.class);
        add.setTotalQuantity(sumQuantity(bo.getDetails()));
        receiptOrderMapper.insert(add);
        bo.setId(add.getId());
        List<ReceiptOrderDetailBo> detailBoList = bo.getDetails();
        List<ReceiptOrderDetail> addDetailList = MapstructUtils.convert(detailBoList, ReceiptOrderDetail.class);
        addDetailList.forEach(it -> {
            it.setOrderId(add.getId());
        });
        // 创建入库单明细
        receiptOrderDetailService.saveDetails(addDetailList);
        return add.getId();
    }

    /**
     * 入库：
     * 1.校验
     * 2.保存入库单和入库单明细
     * 3.增加库存
     * 4.改变库位状态
     * 5.保存库存记录
     */
    @Transactional
    public void receive(ReceiptOrderBo bo) {
        // 0. 合并单据原始信息（PAD 端执行时只回传明细，类型/来源单需从库中补齐）
        if (bo.getId() != null) {
            ReceiptOrder db = receiptOrderMapper.selectById(bo.getId());
            if (db != null) {
                if (bo.getReceiptType() == null) {
                    bo.setReceiptType(db.getReceiptType());
                }
                if (bo.getSourceOrderId() == null) {
                    bo.setSourceOrderId(db.getSourceOrderId());
                }
                if (bo.getSourceOrderNo() == null) {
                    bo.setSourceOrderNo(db.getSourceOrderNo());
                }
                if (bo.getWarehouseId() == null) {
                    bo.setWarehouseId(db.getWarehouseId());
                }
            }
        }
        // 1. 校验
        validateBeforeReceive(bo);
        validateLocations(bo);

        // 2. 保存入库单（已存在的单据执行上架时不修改明细结构，只做数量累计）
        if (Objects.isNull(bo.getId())) {
            insertByBo(bo);
        }

        // 3.增加库存
        inventoryService.add(bo.getDetails());

        // 4.保存库存记录
        inventoryHistoryService.saveInventoryHistory(bo,ServiceConstants.InventoryHistoryOrderType.RECEIPT,true);

        // 5.累计明细与单据的操作数量（实际入库/上架数量）
        if (bo.getId() != null && CollUtil.isNotEmpty(bo.getDetails())) {
            List<ReceiptOrderDetail> detailList = receiptOrderDetailMapper.selectList(
                Wrappers.<ReceiptOrderDetail>lambdaQuery().eq(ReceiptOrderDetail::getOrderId, bo.getId()));
            for (ReceiptOrderDetailBo detailBo : bo.getDetails()) {
                ReceiptOrderDetail matched = null;
                // 优先按明细ID匹配，其次按 SKU + 货位，最后按 SKU 找未满的明细
                if (detailBo.getId() != null) {
                    matched = detailList.stream()
                        .filter(d -> Objects.equals(d.getId(), detailBo.getId()))
                        .findFirst().orElse(null);
                }
                if (matched == null && detailBo.getStockCode() != null) {
                    String stockCode = detailBo.getStockCode().trim();
                    matched = detailList.stream()
                        .filter(d -> Objects.equals(d.getSkuId(), detailBo.getSkuId())
                            && d.getStockCode() != null && stockCode.equals(d.getStockCode().trim()))
                        .findFirst().orElse(null);
                }
                if (matched == null) {
                    matched = detailList.stream()
                        .filter(d -> Objects.equals(d.getSkuId(), detailBo.getSkuId())
                            && (d.getOperatedQuantity() == null || d.getOperatedQuantity().compareTo(d.getQuantity()) < 0))
                        .findFirst().orElse(null);
                }
                if (matched == null) {
                    continue;
                }
                BigDecimal beforeDetail = matched.getOperatedQuantity() == null ? BigDecimal.ZERO : matched.getOperatedQuantity();
                BigDecimal addQty = detailBo.getQuantity() == null ? BigDecimal.ZERO : detailBo.getQuantity();
                BigDecimal plan = matched.getQuantity() == null ? BigDecimal.ZERO : matched.getQuantity();
                if (beforeDetail.add(addQty).compareTo(plan) > 0) {
                    throw new BaseException("上架数量不能超过计划数量");
                }
                matched.setOperatedQuantity(beforeDetail.add(addQty));
                receiptOrderDetailMapper.updateById(matched);
            }
            refreshOrderOperatedQuantity(bo.getId());
        }

        // 6.采购入库：回写采购单入库数量；全部明细操作数量 >= 计划数量才自动关单
        if (ServiceConstants.ReceiptType.PURCHASE.equals(bo.getReceiptType()) && bo.getSourceOrderId() != null) {
            completePurchaseOrder(bo);
        }
    }

    /**
     * 汇总单据操作数量；所有明细已满（操作数量 >= 计划数量）时自动关单，否则保持未入库
     */
    private void refreshOrderOperatedQuantity(Long orderId) {
        List<ReceiptOrderDetail> details = receiptOrderDetailMapper.selectList(
            Wrappers.<ReceiptOrderDetail>lambdaQuery().eq(ReceiptOrderDetail::getOrderId, orderId));
        BigDecimal total = details.stream()
            .map(d -> d.getOperatedQuantity() == null ? BigDecimal.ZERO : d.getOperatedQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean allDone = !details.isEmpty() && details.stream().allMatch(d -> {
            BigDecimal operated = d.getOperatedQuantity() == null ? BigDecimal.ZERO : d.getOperatedQuantity();
            BigDecimal plan = d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity();
            return operated.compareTo(plan) >= 0;
        });
        receiptOrderMapper.update(null, Wrappers.<ReceiptOrder>lambdaUpdate()
            .eq(ReceiptOrder::getId, orderId)
            .set(ReceiptOrder::getOperatedQuantity, total)
            .set(ReceiptOrder::getOrderStatus, allDone
                ? ServiceConstants.ReceiptOrderStatus.FINISH
                : ServiceConstants.ReceiptOrderStatus.PENDING));
    }

    /**
     * 校验上架货位必须存在且属于当前仓库
     */
    private void validateLocations(ReceiptOrderBo bo) {
        if (CollUtil.isEmpty(bo.getDetails())) {
            return;
        }
        for (ReceiptOrderDetailBo detail : bo.getDetails()) {
            String stockCode = detail.getStockCode();
            if (StringUtils.isBlank(stockCode)) {
                continue;
            }
            Long warehouseId = detail.getWarehouseId() != null ? detail.getWarehouseId() : bo.getWarehouseId();
            for (String code : stockCode.split(",")) {
                String trimmed = code.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                boolean exists = inventoryLocationMapper.exists(Wrappers.<InventoryLocation>lambdaQuery()
                    .eq(InventoryLocation::getWarehouseId, warehouseId)
                    .eq(InventoryLocation::getStockCode, trimmed));
                if (!exists) {
                    throw new BaseException("货位【" + trimmed + "】不存在或不属于当前仓库，无法上架");
                }
            }
        }
    }

    /**
     * 采购入库完成后回写采购单
     */
    private void completePurchaseOrder(ReceiptOrderBo bo) {
        List<PurchaseOrderDetail> details = purchaseOrderDetailMapper.selectList(
            Wrappers.<PurchaseOrderDetail>lambdaQuery().eq(PurchaseOrderDetail::getOrderId, bo.getSourceOrderId()));
        Map<Long, PurchaseOrderDetail> detailMap = details.stream()
            .collect(Collectors.toMap(PurchaseOrderDetail::getSkuId, Function.identity(), (a, b) -> a));
        for (ReceiptOrderDetail detail : MapstructUtils.convert(bo.getDetails(), ReceiptOrderDetail.class)) {
            PurchaseOrderDetail poDetail = detailMap.get(detail.getSkuId());
            if (poDetail != null) {
                BigDecimal inbound = poDetail.getInboundQuantity() == null ? BigDecimal.ZERO : poDetail.getInboundQuantity();
                inbound = inbound.add(detail.getQuantity());
                poDetail.setInboundQuantity(inbound);
                BigDecimal returnQty = poDetail.getReturnQuantity() == null ? BigDecimal.ZERO : poDetail.getReturnQuantity();
                poDetail.setActualQuantity(inbound.subtract(returnQty));
                purchaseOrderDetailMapper.updateById(poDetail);
            }
        }
        // 全部明细到货（入库数量 >= 采购数量）才自动关单，否则保持执行中，由用户手动关单
        boolean allReceived = details.stream().allMatch(d -> {
            BigDecimal inbound = d.getInboundQuantity() == null ? BigDecimal.ZERO : d.getInboundQuantity();
            BigDecimal plan = d.getPurchaseQuantity() == null ? BigDecimal.ZERO : d.getPurchaseQuantity();
            return inbound.compareTo(plan) >= 0;
        });
        purchaseOrderMapper.update(null, Wrappers.<PurchaseOrder>lambdaUpdate()
            .eq(PurchaseOrder::getId, bo.getSourceOrderId())
            .set(PurchaseOrder::getOrderStatus, allReceived
                ? ServiceConstants.PurchaseOrderStatus.CLOSED
                : ServiceConstants.PurchaseOrderStatus.RUNNING));
    }

    /**
     * 按采购单生成/同步采购入库单（PC 端不可编辑，状态待入库）
     */
    @Transactional
    public void syncPurchaseReceipt(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectById(purchaseOrderId);
        Assert.notNull(purchaseOrder, "采购单不存在");
        List<PurchaseOrderDetail> poDetails = purchaseOrderDetailMapper.selectList(
            Wrappers.<PurchaseOrderDetail>lambdaQuery().eq(PurchaseOrderDetail::getOrderId, purchaseOrderId));
        ReceiptOrder receipt = receiptOrderMapper.selectOne(Wrappers.<ReceiptOrder>lambdaQuery()
            .eq(ReceiptOrder::getSourceOrderId, purchaseOrderId)
            .eq(ReceiptOrder::getReceiptType, ServiceConstants.ReceiptType.PURCHASE)
            .last("limit 1"));
        if (receipt != null && ServiceConstants.ReceiptOrderStatus.FINISH.equals(receipt.getOrderStatus())) {
            // 已入库，不再同步
            return;
        }
        if (receipt == null) {
            receipt = new ReceiptOrder();
            receipt.setOrderNo("RK" + purchaseOrder.getOrderNo());
            receipt.setReceiptType(ServiceConstants.ReceiptType.PURCHASE);
            receipt.setSourceOrderId(purchaseOrderId);
            receipt.setSourceOrderNo(purchaseOrder.getOrderNo());
            receipt.setOrderStatus(ServiceConstants.ReceiptOrderStatus.PENDING);
            receipt.setWarehouseId(purchaseOrder.getWarehouseId());
            receipt.setTotalQuantity(purchaseOrder.getTotalQuantity());
            receipt.setRemark("由采购单【" + purchaseOrder.getOrderNo() + "】自动生成");
            receiptOrderMapper.insert(receipt);
        } else {
            receiptOrderDetailService.deleteByReceiptOrderId(receipt.getId());
            receiptOrderMapper.update(null, Wrappers.<ReceiptOrder>lambdaUpdate()
                .eq(ReceiptOrder::getId, receipt.getId())
                .set(ReceiptOrder::getWarehouseId, purchaseOrder.getWarehouseId())
                .set(ReceiptOrder::getTotalQuantity, purchaseOrder.getTotalQuantity()));
        }
        List<ReceiptOrderDetail> detailList = new ArrayList<>();
        for (PurchaseOrderDetail poDetail : poDetails) {
            ReceiptOrderDetail detail = new ReceiptOrderDetail();
            detail.setOrderId(receipt.getId());
            detail.setSkuId(poDetail.getSkuId());
            detail.setQuantity(poDetail.getPurchaseQuantity());
            detail.setWarehouseId(purchaseOrder.getWarehouseId());
            detailList.add(detail);
        }
        receiptOrderDetailService.saveDetails(detailList);
    }

    /**
     * 删除采购单对应的未完成入库单
     */
    public void deletePurchaseReceipt(Long purchaseOrderId) {
        List<ReceiptOrder> list = receiptOrderMapper.selectList(Wrappers.<ReceiptOrder>lambdaQuery()
            .eq(ReceiptOrder::getSourceOrderId, purchaseOrderId)
            .eq(ReceiptOrder::getReceiptType, ServiceConstants.ReceiptType.PURCHASE));
        for (ReceiptOrder receipt : list) {
            if (!ServiceConstants.ReceiptOrderStatus.FINISH.equals(receipt.getOrderStatus())) {
                receiptOrderDetailService.deleteByReceiptOrderId(receipt.getId());
                receiptOrderMapper.deleteById(receipt.getId());
            }
        }
    }

    private void validateBeforeReceive(ReceiptOrderBo bo) {
        if (CollUtil.isEmpty(bo.getDetails())) {
            throw new BaseException("商品明细不能为空");
        }
        // 领料返库：必须关联领料出库单，且返库数量不能大于出库数量
        if (ServiceConstants.ReceiptType.RETURN_MATERIAL.equals(bo.getReceiptType())) {
            if (bo.getSourceOrderId() == null) {
                throw new BaseException("领料返库必须关联领料出库单");
            }
            List<ShipmentOrderDetailVo> shipped = shipmentOrderDetailService.queryByShipmentOrderId(bo.getSourceOrderId());
            Map<Long, BigDecimal> shippedMap = new HashMap<>();
            for (ShipmentOrderDetailVo detail : shipped) {
                shippedMap.merge(detail.getSkuId(),
                    detail.getQuantity() == null ? BigDecimal.ZERO : detail.getQuantity(), BigDecimal::add);
            }
            for (ReceiptOrderDetailBo detail : bo.getDetails()) {
                BigDecimal shippedQty = shippedMap.get(detail.getSkuId());
                if (shippedQty == null) {
                    throw new BaseException("返库物料不在关联的领料出库单中");
                }
                if (detail.getQuantity() != null && detail.getQuantity().compareTo(shippedQty) > 0) {
                    throw new BaseException("返库数量不能大于领料出库数量");
                }
            }
        }
    }

    /**
     * 修改入库单
     */
    @Transactional
    public void updateByBo(ReceiptOrderBo bo) {
        // 更新入库单
        ReceiptOrder update = MapstructUtils.convert(bo, ReceiptOrder.class);
        // 计划数量只在建单时写入，执行入库时以数据库原值为准
        update.setTotalQuantity(null);
        receiptOrderMapper.updateById(update);
        // 保存入库单明细
        List<ReceiptOrderDetail> detailList = MapstructUtils.convert(bo.getDetails(), ReceiptOrderDetail.class);
        //需要考虑detail删除
        List<ReceiptOrderDetailVo> dbList = receiptOrderDetailService.queryByReceiptOrderId(bo.getId());
        Set<Long> ids = detailList.stream().filter(it -> it.getId() != null).map(it -> it.getId()).collect(Collectors.toSet());
        List<ReceiptOrderDetailVo> delList = dbList.stream().filter(it -> !ids.contains(it.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(delList)) {
            receiptOrderDetailService.deleteByIds(delList.stream().map(it->it.getId()).collect(Collectors.toList()));
        }
        detailList.forEach(it -> it.setOrderId(bo.getId()));
        receiptOrderDetailService.saveDetails(detailList);
    }

    /**
     * 入库单作废
     * @param id
     */
    public void editToInvalid(Long id) {
        LambdaUpdateWrapper<ReceiptOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ReceiptOrder::getId, id);
        wrapper.set(ReceiptOrder::getOrderStatus, ServiceConstants.ReceiptOrderStatus.INVALID);
        receiptOrderMapper.update(null, wrapper);
    }

    /**
     * 删除入库单
     */
    public void deleteById(Long id) {
        validateIdBeforeDelete(id);
        receiptOrderMapper.deleteById(id);
    }

    private void validateIdBeforeDelete(Long id) {
        ReceiptOrderVo receiptOrderVo = queryById(id);
        Assert.notNull(receiptOrderVo, "入库单不存在");
        if (ServiceConstants.ReceiptOrderStatus.FINISH.equals(receiptOrderVo.getOrderStatus())) {
            throw new ServiceException("删除失败", HttpStatus.CONFLICT,"入库单【" + receiptOrderVo.getOrderNo() + "】已入库，无法删除！");
        }
    }

    /**
     * 批量删除入库单
     */
    public void deleteByIds(Collection<Long> ids) {
        receiptOrderMapper.deleteBatchIds(ids);
    }

    private BigDecimal sumQuantity(List<ReceiptOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return BigDecimal.ZERO;
        }
        return details.stream()
            .map(d -> d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validateReceiptOrderNo(String receiptOrderNo) {        LambdaQueryWrapper<ReceiptOrder> receiptOrderLqw = Wrappers.lambdaQuery();
        receiptOrderLqw.eq(ReceiptOrder::getOrderNo, receiptOrderNo);
        ReceiptOrder receiptOrder = receiptOrderMapper.selectOne(receiptOrderLqw);
        Assert.isNull(receiptOrder, "入库单号重复，请手动修改");
    }
}
