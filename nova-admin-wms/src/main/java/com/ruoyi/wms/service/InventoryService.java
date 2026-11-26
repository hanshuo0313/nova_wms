package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.BaseOrderDetailBo;
import com.ruoyi.wms.domain.bo.CheckOrderDetailBo;
import com.ruoyi.wms.domain.bo.InventoryBo;
import com.ruoyi.wms.domain.bo.InventoryLocationBo;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.ruoyi.wms.domain.vo.InventoryVo;
import com.ruoyi.wms.domain.vo.ItemSkuMapVo;
import com.ruoyi.wms.mapper.InventoryLocationMapper;
import com.ruoyi.wms.mapper.InventoryMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存Service业务层处理
 *
 * @author zcc
 * @date 2024-07-19
 */
@RequiredArgsConstructor
@Service
public class InventoryService extends ServiceImpl<InventoryMapper, Inventory> {

    private final InventoryMapper inventoryMapper;
    private final ItemSkuService itemSkuService;
    private final InventoryLocationMapper inventoryLocationMapper;


    /**
     * 查询库存
     */
    public InventoryVo queryById(Long id){
        return inventoryMapper.selectVoById(id);
    }

    /**
     * 查询库存列表
     */
    public List<InventoryVo> queryList(InventoryBo bo) {
        LambdaQueryWrapper<Inventory> lqw = buildQueryWrapper(bo);
        List<InventoryVo> vos = inventoryMapper.selectVoList(lqw);
        if(CollUtil.isNotEmpty(vos)){
            Set<Long> skuIds = vos.stream().map(InventoryVo::getSkuId).collect(Collectors.toSet());
            Map<Long, ItemSkuMapVo> itemSkuMap = itemSkuService.queryItemSkuMapVosByIds(skuIds);
            vos.forEach(it -> {
                ItemSkuMapVo itemSkuMapVo = itemSkuMap.get(it.getSkuId());
                it.setItemSku(itemSkuMapVo.getItemSku());
                it.setItem(itemSkuMapVo.getItem());
            });
        }
        return vos;
    }

    private LambdaQueryWrapper<Inventory> buildQueryWrapper(InventoryBo bo) {
        LambdaQueryWrapper<Inventory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(bo.getSkuId() != null, Inventory::getSkuId, bo.getSkuId());
        wrapper.eq(bo.getWarehouseId() != null, Inventory::getWarehouseId, bo.getWarehouseId());
        wrapper.eq(bo.getQuantity() != null, Inventory::getQuantity, bo.getQuantity());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        wrapper.in(CollUtil.isNotEmpty(warehouseScopeIds), Inventory::getWarehouseId, warehouseScopeIds);
        return wrapper;
    }

    /**
     * 新增库存
     */
    public void insertByBo(InventoryBo bo) {
        Inventory add = MapstructUtils.convert(bo, Inventory.class);
        inventoryMapper.insert(add);
    }

    /**
     * 修改库存
     */
    public void updateByBo(InventoryBo bo) {
        Inventory update = MapstructUtils.convert(bo, Inventory.class);
        inventoryMapper.updateById(update);
    }

    /**
     * 批量删除库存
     */
    public void deleteByIds(Collection<Long> ids) {
        inventoryMapper.deleteBatchIds(ids);
    }

    /**
     * 校验规格是否有库存
     * @param skuIds
     * @return
     */
    public boolean existsBySkuIds(@NotEmpty Collection<Long> skuIds) {
        LambdaQueryWrapper<Inventory> lqw = Wrappers.lambdaQuery();
        lqw.in(Inventory::getSkuId, skuIds);
        return inventoryMapper.exists(lqw);
    }

    public TableDataInfo<InventoryVo> queryWarehouseBoardList(InventoryBo bo, PageQuery pageQuery) {
            return TableDataInfo.build(inventoryMapper.queryWarehouseBoardList(pageQuery.build(), bo, WarehouseScopeUtils.getWarehouseIds()));
    }

    public TableDataInfo<InventoryVo> queryItemBoardList(InventoryBo bo, PageQuery pageQuery) {
        Page<InventoryVo> result = inventoryMapper.queryItemBoardList(pageQuery.build(), bo, WarehouseScopeUtils.getWarehouseIds());
        return TableDataInfo.build(result);
    }

    /**
     * 库存查询（含供应商/分类/等级/型号/规格/动碰次数）
     */
    public TableDataInfo<InventoryVo> queryPageList(InventoryBo bo, PageQuery pageQuery) {
        return TableDataInfo.build(inventoryMapper.queryPageList(pageQuery.build(), bo));
    }

    /**
     * PAD 库存查询：按仓库 + SKU 汇总库存数量
     */
    public TableDataInfo<InventoryVo> querySummaryPage(InventoryBo bo, PageQuery pageQuery) {
        return TableDataInfo.build(inventoryMapper.selectSummaryPage(pageQuery.build(), bo));
    }

    /**
     * 安全库存预警：按仓库比较现有库存与物料安全库存水位
     */
    public TableDataInfo<InventoryVo> querySafetyStockAlert(InventoryBo bo, PageQuery pageQuery) {
        return TableDataInfo.build(inventoryMapper.selectSafetyStockPage(pageQuery.build(), bo));
    }

    /**
     * 不分页查询库存（盘点任务生成等场景）
     */
    public List<InventoryVo> queryAll(InventoryBo bo) {
        return inventoryMapper.queryPageList(new Page<>(1, 20000), bo).getRecords();
    }

    /**
     * 盘点完成后清零动碰次数
     */
    public void resetMoveCount(Collection<Long> inventoryIds) {
        if (CollUtil.isEmpty(inventoryIds)) {
            return;
        }
        LambdaUpdateWrapper<Inventory> wrapper = Wrappers.lambdaUpdate();
        wrapper.in(Inventory::getId, inventoryIds);
        wrapper.set(Inventory::getMoveCount, 0);
        inventoryMapper.update(null, wrapper);
    }

    /**
     * 盘点完成后按仓库+规格清零动碰次数
     */
    public void resetMoveCountBySku(Long warehouseId, Collection<Long> skuIds) {
        if (warehouseId == null || CollUtil.isEmpty(skuIds)) {
            return;
        }
        LambdaUpdateWrapper<Inventory> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Inventory::getWarehouseId, warehouseId);
        wrapper.in(Inventory::getSkuId, skuIds);
        wrapper.set(Inventory::getMoveCount, 0);
        inventoryMapper.update(null, wrapper);
    }

    public void updateInventory(List<CheckOrderDetailBo> details) {
        List<Inventory> updateInventoryList=new LinkedList<>();
        List<Inventory> insertInventoryList=new LinkedList<>();

        details.forEach(detail -> {
            String code = detail.getStockCode();
            if (code != null && code.contains(",")) {
                code = code.split(",")[0];
            }
            Inventory inventory = null;
            if (detail.getInventoryId() != null) {
                inventory = inventoryMapper.selectById(detail.getInventoryId());
            }
            if (inventory == null) {
                inventory = findInventory(detail.getWarehouseId(), detail.getSkuId(), code);
            }
            if (inventory != null) {
                BigDecimal book = inventory.getQuantity() == null ? BigDecimal.ZERO : inventory.getQuantity();
                if (detail.getQuantity() != null && book.compareTo(detail.getQuantity()) != 0) {
                    ItemSkuMapVo itemSkuMapVo = itemSkuService.queryItemSkuMapVo(detail.getSkuId());
                    throw new ServiceException(
                        "账面库存不匹配：" + itemSkuMapVo.getItem().getItemName() + "（" + itemSkuMapVo.getItemSku().getSkuName() + "）",
                        HttpStatus.CONFLICT,
                        "填写账面库存：" + detail.getQuantity() + " 实际账面库存：" + book);
                }
                if (!book.equals(detail.getCheckQuantity())) {
                    inventory.setQuantity(detail.getCheckQuantity());
                    // 盘点确认后释放该库存行锁定量
                    inventory.setLockedQuantity(BigDecimal.ZERO);
                    if (code != null && !code.trim().isEmpty()) {
                        inventory.setStockCode(code.trim());
                    }
                    updateInventoryList.add(inventory);
                }
            } else {
                Inventory add = new Inventory();
                add.setSkuId(detail.getSkuId());
                add.setWarehouseId(detail.getWarehouseId());
                add.setQuantity(detail.getCheckQuantity());
                add.setStockCode(code == null ? null : code.trim());
                insertInventoryList.add(add);
            }
        });
        if(CollUtil.isNotEmpty(updateInventoryList)){
            inventoryMapper.updateBatchById(updateInventoryList);
        }
        if(CollUtil.isNotEmpty(insertInventoryList)){
            inventoryMapper.insertBatch(insertInventoryList);
        }
        // 库存为 0 时删除该条库存记录
        inventoryMapper.delete(Wrappers.<Inventory>lambdaQuery().le(Inventory::getQuantity, BigDecimal.ZERO));
        // 盘点后刷新库位状态
        details.forEach(d -> refreshLocationStatuses(d.getWarehouseId(), d.getStockCode()));
    }

    @Transactional
    public void add(List<? extends BaseOrderDetailBo> details) {
        List<Inventory> addList = new LinkedList<>();
        List<Inventory> updateList = new LinkedList<>();
        details.forEach(orderDetailsBo -> {
            // 库存按货位分行：同一物料不同货位各一条库存记录
            List<String> codes = splitStockCodes(orderDetailsBo.getStockCode());
            List<BigDecimal> shares = splitQuantity(orderDetailsBo.getQuantity(), codes.size());
            BigDecimal beforeTotal = BigDecimal.ZERO;
            BigDecimal afterTotal = BigDecimal.ZERO;
            for (int i = 0; i < codes.size(); i++) {
                String code = codes.get(i);
                BigDecimal share = shares.get(i);
                Inventory result = findInventory(orderDetailsBo.getWarehouseId(), orderDetailsBo.getSkuId(), code);
                BigDecimal before = result == null || result.getQuantity() == null ? BigDecimal.ZERO : result.getQuantity();
                BigDecimal after = before.add(share);
                if (result != null) {
                    result.setQuantity(after);
                    result.setMoveCount(result.getMoveCount() == null ? 1 : result.getMoveCount() + 1);
                    updateList.add(result);
                } else {
                    Inventory inventory = new Inventory();
                    inventory.setSkuId(orderDetailsBo.getSkuId());
                    inventory.setWarehouseId(orderDetailsBo.getWarehouseId());
                    inventory.setStockCode(code);
                    inventory.setQuantity(share);
                    inventory.setMoveCount(1);
                    addList.add(inventory);
                }
                beforeTotal = beforeTotal.add(before);
                afterTotal = afterTotal.add(after);
            }
            orderDetailsBo.setBeforeQuantity(beforeTotal);
            orderDetailsBo.setAfterQuantity(afterTotal);
            //改变库位状态：有库存使用则标记为使用中
            markLocationsUsed(orderDetailsBo.getWarehouseId(), orderDetailsBo.getStockCode());
        });
        if (addList.size() > 0) {
            saveBatch(addList);
        }
        if (updateList.size() > 0) {
            updateBatchById(updateList);
        }
    }

    /**
     * 拆分货位编码（空值返回单个 null，保持无货位库存）
     */
    private List<String> splitStockCodes(String stockCodes) {
        List<String> codes = new LinkedList<>();
        if (stockCodes != null && !stockCodes.trim().isEmpty()) {
            for (String code : stockCodes.split(",")) {
                if (!code.trim().isEmpty()) {
                    codes.add(code.trim());
                }
            }
        }
        if (codes.isEmpty()) {
            codes.add(null);
        }
        return codes;
    }

    /**
     * 数量按货位均分，除不尽时余数放到最后一个货位
     */
    private List<BigDecimal> splitQuantity(BigDecimal total, int count) {
        List<BigDecimal> list = new LinkedList<>();
        if (count <= 0) {
            return list;
        }
        BigDecimal safeTotal = total == null ? BigDecimal.ZERO : total;
        if (count == 1) {
            list.add(safeTotal);
            return list;
        }
        BigDecimal share = safeTotal.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.DOWN);
        BigDecimal assigned = BigDecimal.ZERO;
        for (int i = 0; i < count - 1; i++) {
            list.add(share);
            assigned = assigned.add(share);
        }
        list.add(safeTotal.subtract(assigned));
        return list;
    }

    /**
     * 按仓库+SKU+货位查询库存（货位为空时匹配空货位）
     */
    private Inventory findInventory(Long warehouseId, Long skuId, String stockCode) {
        LambdaQueryWrapper<Inventory> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Inventory::getWarehouseId, warehouseId);
        wrapper.eq(Inventory::getSkuId, skuId);
        if (stockCode == null || stockCode.trim().isEmpty()) {
            wrapper.and(w -> w.isNull(Inventory::getStockCode).or().eq(Inventory::getStockCode, ""));
        } else {
            wrapper.eq(Inventory::getStockCode, stockCode.trim());
        }
        List<Inventory> list = inventoryMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 扣减库存
     * @param details
     */
    @Transactional
    public void subtract(List<? extends BaseOrderDetailBo> details) {
        List<Inventory> updateList = new LinkedList<>();
        List<Long> deleteIds = new LinkedList<>();
        details.forEach(shipmentOrderDetailBo -> {
            // 按货位逐行扣减：同一物料不同货位分别扣减，库存为 0 删除该行
            List<String> codes = splitStockCodes(shipmentOrderDetailBo.getStockCode());
            BigDecimal remaining = shipmentOrderDetailBo.getQuantity() == null ? BigDecimal.ZERO : shipmentOrderDetailBo.getQuantity();
            BigDecimal beforeTotal = BigDecimal.ZERO;
            BigDecimal afterTotal = BigDecimal.ZERO;
            for (String code : codes) {
                Inventory result = findInventory(shipmentOrderDetailBo.getWarehouseId(), shipmentOrderDetailBo.getSkuId(), code);
                if (result == null || result.getQuantity() == null) {
                    continue;
                }
                BigDecimal before = result.getQuantity();
                BigDecimal consume = before.min(remaining);
                if (consume.signum() <= 0) {
                    continue;
                }
                BigDecimal after = before.subtract(consume);
                result.setQuantity(after);
                result.setMoveCount(result.getMoveCount() == null ? 1 : result.getMoveCount() + 1);
                if (after.signum() <= 0) {
                    deleteIds.add(result.getId());
                } else {
                    updateList.add(result);
                }
                beforeTotal = beforeTotal.add(before);
                afterTotal = afterTotal.add(after);
                remaining = remaining.subtract(consume);
                if (remaining.signum() <= 0) {
                    break;
                }
            }
            if (remaining.signum() > 0) {
                ItemSkuMapVo itemSkuMapVo = itemSkuService.queryItemSkuMapVo(shipmentOrderDetailBo.getSkuId());
                throw new ServiceException("库存不足", HttpStatus.CONFLICT,
                    itemSkuMapVo.getItem().getItemName() + "（" + itemSkuMapVo.getItemSku().getSkuName() + "）库存不足，缺少：" + remaining);
            }
            shipmentOrderDetailBo.setBeforeQuantity(beforeTotal);
            shipmentOrderDetailBo.setAfterQuantity(afterTotal);
        });
        if (!deleteIds.isEmpty()) {
            inventoryMapper.deleteBatchIds(deleteIds);
        }
        if (!updateList.isEmpty()) {
            updateBatchById(updateList);
        }
        // 出库后刷新库位状态（无库存则置为空闲）
        details.forEach(d -> refreshLocationStatuses(d.getWarehouseId(), d.getStockCode()));
    }

    /**
     * 锁定库存：按货位行依次锁定，单行可用不足时命中多行
     */
    @Transactional
    public List<Allocation> allocate(Long warehouseId, Long skuId, BigDecimal quantity) {
        List<Inventory> rows = inventoryMapper.selectList(Wrappers.<Inventory>lambdaQuery()
            .eq(Inventory::getWarehouseId, warehouseId)
            .eq(Inventory::getSkuId, skuId)
            .gt(Inventory::getQuantity, BigDecimal.ZERO)
            .orderByAsc(Inventory::getStockCode)
            .last("for update"));
        BigDecimal remaining = quantity == null ? BigDecimal.ZERO : quantity;
        List<Allocation> allocations = new LinkedList<>();
        for (Inventory row : rows) {
            if (remaining.signum() <= 0) {
                break;
            }
            BigDecimal locked = row.getLockedQuantity() == null ? BigDecimal.ZERO : row.getLockedQuantity();
            BigDecimal available = row.getQuantity().subtract(locked);
            if (available.signum() <= 0) {
                continue;
            }
            BigDecimal take = available.min(remaining);
            row.setLockedQuantity(locked.add(take));
            inventoryMapper.updateById(row);
            allocations.add(new Allocation(row.getId(), row.getStockCode(), take));
            remaining = remaining.subtract(take);
        }
        if (remaining.signum() > 0) {
            ItemSkuMapVo itemSkuMapVo = itemSkuService.queryItemSkuMapVo(skuId);
            String itemName = itemSkuMapVo == null || itemSkuMapVo.getItem() == null
                ? String.valueOf(skuId) : itemSkuMapVo.getItem().getItemName();
            throw new ServiceException("可用库存不足", HttpStatus.CONFLICT,
                itemName + " 可用库存不足，缺少：" + remaining);
        }
        return allocations;
    }

    /**
     * 锁定指定库存行
     */
    public void lock(Long inventoryId, BigDecimal quantity) {
        if (inventoryId == null || quantity == null || quantity.signum() <= 0) {
            return;
        }
        Inventory row = inventoryMapper.selectById(inventoryId);
        if (row == null) {
            return;
        }
        BigDecimal locked = row.getLockedQuantity() == null ? BigDecimal.ZERO : row.getLockedQuantity();
        BigDecimal available = row.getQuantity().subtract(locked);
        if (available.compareTo(quantity) < 0) {
            throw new ServiceException("可用库存不足，无法锁定：" + row.getStockCode());
        }
        row.setLockedQuantity(locked.add(quantity));
        inventoryMapper.updateById(row);
    }

    /**
     * 锁定该库存行全部可用数量（缺货时冻结该货位库存）
     */
    public void lockAll(Long inventoryId) {
        if (inventoryId == null) {
            return;
        }
        Inventory row = inventoryMapper.selectById(inventoryId);
        if (row == null || row.getQuantity() == null) {
            return;
        }
        row.setLockedQuantity(row.getQuantity());
        inventoryMapper.updateById(row);
    }

    /**
     * 锁定某货位该 SKU 的全部库存（缺货冻结货位）
     */
    public void lockLocation(Long warehouseId, Long skuId, String stockCode) {
        if (warehouseId == null || skuId == null || stockCode == null) {
            return;
        }
        List<Inventory> rows = inventoryMapper.selectList(Wrappers.<Inventory>lambdaQuery()
            .eq(Inventory::getWarehouseId, warehouseId)
            .eq(Inventory::getSkuId, skuId)
            .eq(Inventory::getStockCode, stockCode.trim()));
        for (Inventory row : rows) {
            if (row.getQuantity() != null) {
                row.setLockedQuantity(row.getQuantity());
                inventoryMapper.updateById(row);
            }
        }
    }

    /**
     * 释放锁定库存
     */
    public void releaseLock(Long inventoryId, BigDecimal quantity) {
        if (inventoryId == null || quantity == null || quantity.signum() <= 0) {
            return;
        }
        Inventory row = inventoryMapper.selectById(inventoryId);
        if (row == null) {
            return;
        }
        BigDecimal locked = row.getLockedQuantity() == null ? BigDecimal.ZERO : row.getLockedQuantity();
        row.setLockedQuantity(locked.subtract(quantity).max(BigDecimal.ZERO));
        inventoryMapper.updateById(row);
    }

    /**
     * 消耗锁定库存（出库/移库拣选时：库存与锁定同时减少）
     */
    @Transactional
    public Inventory consumeLocked(Long inventoryId, BigDecimal quantity) {
        Inventory row = inventoryMapper.selectById(inventoryId);
        if (row == null) {
            throw new ServiceException("库存记录不存在，无法出库");
        }
        BigDecimal before = row.getQuantity();
        BigDecimal locked = row.getLockedQuantity() == null ? BigDecimal.ZERO : row.getLockedQuantity();
        BigDecimal after = before.subtract(quantity);
        if (after.signum() < 0) {
            throw new ServiceException("库存不足，当前库存：" + before);
        }
        row.setQuantity(after);
        row.setLockedQuantity(locked.subtract(quantity).max(BigDecimal.ZERO));
        row.setMoveCount(row.getMoveCount() == null ? 1 : row.getMoveCount() + 1);
        if (after.signum() <= 0) {
            inventoryMapper.deleteById(row.getId());
        } else {
            inventoryMapper.updateById(row);
        }
        return row;
    }

    /**
     * 配货分配结果
     */
    public static class Allocation {
        private final Long inventoryId;
        private final String stockCode;
        private final BigDecimal quantity;
        public Allocation(Long inventoryId, String stockCode, BigDecimal quantity) {
            this.inventoryId = inventoryId;
            this.stockCode = stockCode;
            this.quantity = quantity;
        }
        public Long getInventoryId() { return inventoryId; }
        public String getStockCode() { return stockCode; }
        public BigDecimal getQuantity() { return quantity; }
    }

    /**
     * 查询某仓库某 SKU 的可用库存（库存-锁定）合计
     */
    public BigDecimal availableQuantity(Long warehouseId, Long skuId) {
        List<Inventory> list = inventoryMapper.selectList(Wrappers.<Inventory>lambdaQuery()
            .eq(warehouseId != null, Inventory::getWarehouseId, warehouseId)
            .eq(skuId != null, Inventory::getSkuId, skuId));
        BigDecimal total = BigDecimal.ZERO;
        for (Inventory row : list) {
            BigDecimal qty = row.getQuantity() == null ? BigDecimal.ZERO : row.getQuantity();
            BigDecimal locked = row.getLockedQuantity() == null ? BigDecimal.ZERO : row.getLockedQuantity();
            total = total.add(qty.subtract(locked));
        }
        return total;
    }

    public boolean existsByWarehouseId(Long warehouseId) {
        LambdaQueryWrapper<Inventory> lqw = Wrappers.lambdaQuery();
        lqw.eq(Inventory::getWarehouseId, warehouseId);
        return inventoryMapper.exists(lqw);
    }

    /**
     * 标记库位为使用中
     */
    private void markLocationsUsed(Long warehouseId, String stockCodes) {
        if (warehouseId == null || stockCodes == null || stockCodes.trim().isEmpty()) {
            return;
        }
        for (String code : stockCodes.split(",")) {
            String trimmed = code.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            inventoryLocationMapper.update(null, Wrappers.<InventoryLocation>lambdaUpdate()
                .eq(InventoryLocation::getWarehouseId, warehouseId)
                .eq(InventoryLocation::getStockCode, trimmed)
                .set(InventoryLocation::getStatus, 2));
        }
    }

    /**
     * 按实际库存刷新库位状态：有库存=使用中(2)，无库存=空闲(1)
     */
    private void refreshLocationStatuses(Long warehouseId, String stockCodes) {
        if (warehouseId == null || stockCodes == null || stockCodes.trim().isEmpty()) {
            return;
        }
        for (String code : stockCodes.split(",")) {
            String trimmed = code.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Long count = inventoryMapper.selectCount(Wrappers.<Inventory>lambdaQuery()
                .eq(Inventory::getWarehouseId, warehouseId)
                .gt(Inventory::getQuantity, BigDecimal.ZERO)
                .apply("FIND_IN_SET({0}, REPLACE(stock_code, ' ', '')) > 0", trimmed));
            inventoryLocationMapper.update(null, Wrappers.<InventoryLocation>lambdaUpdate()
                .eq(InventoryLocation::getWarehouseId, warehouseId)
                .eq(InventoryLocation::getStockCode, trimmed)
                .set(InventoryLocation::getStatus, count != null && count > 0 ? 2 : 1));
        }
    }
}
