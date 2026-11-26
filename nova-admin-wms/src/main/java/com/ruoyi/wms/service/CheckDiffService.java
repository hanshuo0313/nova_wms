package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.wms.domain.bo.CheckOrderBo;
import com.ruoyi.wms.domain.bo.CheckOrderDetailBo;
import com.ruoyi.wms.domain.entity.CheckDiffDetail;
import com.ruoyi.wms.domain.entity.CheckDiffOrder;
import com.ruoyi.wms.domain.entity.CheckOrder;
import com.ruoyi.wms.domain.entity.CheckOrderDetail;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.mapper.CheckDiffDetailMapper;
import com.ruoyi.wms.mapper.CheckDiffOrderMapper;
import com.ruoyi.wms.mapper.CheckOrderMapper;
import com.ruoyi.wms.mapper.CheckOrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 盘点差异单
 */
@RequiredArgsConstructor
@Service
public class CheckDiffService {

    private final CheckDiffOrderMapper checkDiffOrderMapper;
    private final CheckDiffDetailMapper checkDiffDetailMapper;
    private final CheckOrderMapper checkOrderMapper;
    private final CheckOrderDetailMapper checkOrderDetailMapper;
    private final InventoryService inventoryService;
    private final InventoryHistoryService inventoryHistoryService;

    /**
     * 追加差异明细（同一盘点单未确认的差异单合并）
     */
    @Transactional
    public void appendDiff(CheckOrder order, CheckOrderDetail detail, BigDecimal book, BigDecimal checkQty, BigDecimal diff) {
        CheckDiffOrder diffOrder = checkDiffOrderMapper.selectOne(Wrappers.<CheckDiffOrder>lambdaQuery()
            .eq(CheckDiffOrder::getCheckOrderId, order.getId())
            .eq(CheckDiffOrder::getStatus, 0)
            .last("limit 1"));
        if (diffOrder == null) {
            diffOrder = new CheckDiffOrder();
            diffOrder.setOrderNo("CY" + order.getOrderNo());
            diffOrder.setCheckOrderId(order.getId());
            diffOrder.setCheckOrderNo(order.getOrderNo());
            diffOrder.setWarehouseId(order.getWarehouseId());
            diffOrder.setStatus(0);
            diffOrder.setTotalDiff(BigDecimal.ZERO);
            checkDiffOrderMapper.insert(diffOrder);
        }
        CheckDiffDetail diffDetail = new CheckDiffDetail();
        diffDetail.setDiffOrderId(diffOrder.getId());
        diffDetail.setCheckDetailId(detail.getId());
        diffDetail.setSkuId(detail.getSkuId());
        diffDetail.setWarehouseId(detail.getWarehouseId());
        diffDetail.setInventoryId(detail.getInventoryId());
        diffDetail.setStockCode(detail.getStockCode());
        diffDetail.setBookQuantity(book);
        diffDetail.setCheckQuantity(checkQty);
        diffDetail.setDiffQuantity(diff);
        checkDiffDetailMapper.insert(diffDetail);

        BigDecimal total = diffOrder.getTotalDiff() == null ? BigDecimal.ZERO : diffOrder.getTotalDiff();
        diffOrder.setTotalDiff(total.add(diff.abs()));
        checkDiffOrderMapper.updateById(diffOrder);

        // 盘点单置为待差异确认
        checkOrderMapper.update(null, Wrappers.<CheckOrder>lambdaUpdate()
            .eq(CheckOrder::getId, order.getId())
            .set(CheckOrder::getOrderStatus, 2));
    }

    /**
     * 是否存在待确认差异单
     */
    public boolean hasPendingDiff(Long checkOrderId) {
        return checkDiffOrderMapper.exists(Wrappers.<CheckDiffOrder>lambdaQuery()
            .eq(CheckDiffOrder::getCheckOrderId, checkOrderId)
            .eq(CheckDiffOrder::getStatus, 0));
    }

    /**
     * 取消差异单：不调整库存，释放锁定并结束盘点单
     */
    @Transactional
    public void cancel(Long id) {
        CheckDiffOrder diffOrder = checkDiffOrderMapper.selectById(id);
        if (diffOrder == null) {
            throw new BaseException("差异单不存在");
        }
        if (diffOrder.getStatus() != null && diffOrder.getStatus() == 1) {
            throw new BaseException("差异单已确认，不能取消");
        }
        List<CheckDiffDetail> details = checkDiffDetailMapper.selectList(Wrappers.<CheckDiffDetail>lambdaQuery()
            .eq(CheckDiffDetail::getDiffOrderId, id));
        for (CheckDiffDetail diff : details) {
            if (diff.getInventoryId() != null) {
                inventoryService.releaseLock(diff.getInventoryId(), diff.getBookQuantity());
            }
        }
        checkDiffOrderMapper.update(null, Wrappers.<CheckDiffOrder>lambdaUpdate()
            .eq(CheckDiffOrder::getId, id)
            .set(CheckDiffOrder::getStatus, 2));
        checkOrderMapper.update(null, Wrappers.<CheckOrder>lambdaUpdate()
            .eq(CheckOrder::getId, diffOrder.getCheckOrderId())
            .set(CheckOrder::getOrderStatus, ServiceConstants.CheckOrderStatus.FINISH));
    }

    /**
     * 确认差异单：按差异更新库存
     */
    @Transactional
    public void confirm(Long id) {
        CheckDiffOrder diffOrder = checkDiffOrderMapper.selectById(id);
        if (diffOrder == null) {
            throw new BaseException("差异单不存在");
        }
        if (diffOrder.getStatus() != null && diffOrder.getStatus() == 1) {
            throw new BaseException("差异单已确认");
        }
        List<CheckDiffDetail> details = checkDiffDetailMapper.selectList(Wrappers.<CheckDiffDetail>lambdaQuery()
            .eq(CheckDiffDetail::getDiffOrderId, id));
        List<CheckOrderDetailBo> historyDetails = new LinkedList<>();
        for (CheckDiffDetail diff : details) {
            Inventory inventory = diff.getInventoryId() != null ? inventoryService.getById(diff.getInventoryId()) : null;
            if (inventory == null) {
                List<Inventory> list = inventoryService.list(Wrappers.<Inventory>lambdaQuery()
                    .eq(Inventory::getWarehouseId, diff.getWarehouseId())
                    .eq(Inventory::getSkuId, diff.getSkuId())
                    .eq(Inventory::getStockCode, diff.getStockCode()));
                inventory = list.isEmpty() ? null : list.get(0);
            }
            BigDecimal newQty = diff.getCheckQuantity() == null ? BigDecimal.ZERO : diff.getCheckQuantity();
            if (inventory != null) {
                inventory.setQuantity(newQty);
                inventory.setLockedQuantity(BigDecimal.ZERO);
                if (newQty.signum() <= 0) {
                    inventoryService.removeById(inventory.getId());
                } else {
                    inventoryService.updateById(inventory);
                }
            } else if (newQty.signum() > 0) {
                Inventory add = new Inventory();
                add.setWarehouseId(diff.getWarehouseId());
                add.setSkuId(diff.getSkuId());
                add.setStockCode(diff.getStockCode());
                add.setQuantity(newQty);
                add.setLockedQuantity(BigDecimal.ZERO);
                add.setMoveCount(0);
                inventoryService.save(add);
            }
            CheckOrderDetailBo historyDetail = new CheckOrderDetailBo();
            historyDetail.setSkuId(diff.getSkuId());
            historyDetail.setWarehouseId(diff.getWarehouseId());
            historyDetail.setStockCode(diff.getStockCode());
            historyDetail.setQuantity(diff.getDiffQuantity());
            historyDetail.setCheckQuantity(newQty);
            historyDetail.setBeforeQuantity(diff.getBookQuantity());
            historyDetail.setAfterQuantity(newQty);
            historyDetails.add(historyDetail);
            if (diff.getCheckDetailId() != null) {
                CheckOrderDetail checkDetail = checkOrderDetailMapper.selectById(diff.getCheckDetailId());
                if (checkDetail != null) {
                    checkDetail.setCheckQuantity(newQty);
                    checkDetail.setChecked(1);
                    checkOrderDetailMapper.updateById(checkDetail);
                }
            }
        }
        if (!historyDetails.isEmpty()) {
            CheckOrderBo historyBo = new CheckOrderBo();
            historyBo.setId(diffOrder.getCheckOrderId());
            historyBo.setOrderNo(diffOrder.getCheckOrderNo());
            historyBo.setWarehouseId(diffOrder.getWarehouseId());
            historyBo.setDetails(historyDetails);
            inventoryHistoryService.saveInventoryHistory(historyBo, ServiceConstants.InventoryHistoryOrderType.CHECK, true);
            historyDetails.stream().map(CheckOrderDetailBo::getSkuId).distinct()
                .forEach(skuId -> inventoryService.resetMoveCountBySku(diffOrder.getWarehouseId(), Collections.singletonList(skuId)));
        }
        checkDiffOrderMapper.update(null, Wrappers.<CheckDiffOrder>lambdaUpdate()
            .eq(CheckDiffOrder::getId, id)
            .set(CheckDiffOrder::getStatus, 1));
        checkOrderMapper.update(null, Wrappers.<CheckOrder>lambdaUpdate()
            .eq(CheckOrder::getId, diffOrder.getCheckOrderId())
            .set(CheckOrder::getOrderStatus, ServiceConstants.CheckOrderStatus.FINISH));
    }
}
