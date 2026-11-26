package com.ruoyi.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.BaseOrderBo;
import com.ruoyi.wms.domain.bo.BaseOrderDetailBo;
import com.ruoyi.wms.domain.bo.InventoryHistoryBo;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.entity.InventoryHistory;
import com.ruoyi.wms.domain.vo.InventoryHistoryVo;
import com.ruoyi.wms.mapper.InventoryHistoryMapper;
import com.ruoyi.wms.mapper.InventoryMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存记录Service业务层处理
 *
 * @author zcc
 * @date 2024-07-22
 */
@RequiredArgsConstructor
@Service
public class InventoryHistoryService extends ServiceImpl<InventoryHistoryMapper, InventoryHistory> {

    private final InventoryMapper inventoryMapper;
    private final InventoryHistoryMapper inventoryHistoryMapper;

    public void saveInventoryHistory(BaseOrderBo<? extends BaseOrderDetailBo> bo,Integer orderType,Boolean isAdd){
        List<InventoryHistory> inventoryHistoryList = new LinkedList<>();
        bo.getDetails().forEach(detail -> {
            // 每个货位一条库存记录：同一物料不同货位分别记录
            List<String> codes = new LinkedList<>();
            String stockCodes = detail.getStockCode();
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
            // 多个货位时按数量均分，保证每个货位一条记录且数量合计不变
            BigDecimal totalQty = detail.getQuantity() == null ? BigDecimal.ZERO : detail.getQuantity();
            int codeCount = codes.size();
            BigDecimal share = codeCount > 0 ? totalQty.divide(BigDecimal.valueOf(codeCount), 2, java.math.RoundingMode.DOWN) : totalQty;
            BigDecimal assigned = BigDecimal.ZERO;
            for (int i = 0; i < codeCount; i++) {
                String code = codes.get(i);
                BigDecimal qty = (i == codeCount - 1) ? totalQty.subtract(assigned) : share;
                assigned = assigned.add(qty);
                InventoryHistory inventoryHistory = new InventoryHistory();
                inventoryHistory.setOrderId(bo.getId());
                inventoryHistory.setOrderNo(bo.getOrderNo());
                inventoryHistory.setOrderType(orderType);
                inventoryHistory.setSkuId(detail.getSkuId());
                inventoryHistory.setStockCode(code);
                if(isAdd){
                    inventoryHistory.setQuantity(qty);
                } else {
                    inventoryHistory.setQuantity(qty.negate());
                }
                inventoryHistory.setWarehouseId(detail.getWarehouseId());
                inventoryHistory.setAmount(detail.getAmount());
                inventoryHistory.setBeforeQuantity(detail.getBeforeQuantity());
                inventoryHistory.setAfterQuantity(detail.getAfterQuantity());
                inventoryHistoryList.add(inventoryHistory);
            }
        });
        this.saveBatch(inventoryHistoryList);
    }

    /**
     * 查询库存记录
     */
    public InventoryHistoryVo queryById(Long id){
        return inventoryHistoryMapper.selectVoById(id);
    }

    /**
     * 查询库存记录列表
     */
    public TableDataInfo<InventoryHistoryVo> queryPageList(InventoryHistoryBo bo, PageQuery pageQuery) {
        Page<InventoryHistoryVo> result = inventoryHistoryMapper.selectVoPageByBo(pageQuery.build(), bo, WarehouseScopeUtils.getWarehouseIds());
        return TableDataInfo.build(result);
    }

    /**
     * 查询库存记录列表
     */
    public List<InventoryHistoryVo> queryList(InventoryHistoryBo bo) {
        LambdaQueryWrapper<InventoryHistory> lqw = buildQueryWrapper(bo);
        return inventoryHistoryMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<InventoryHistory> buildQueryWrapper(InventoryHistoryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<InventoryHistory> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getOrderId() != null, InventoryHistory::getOrderId, bo.getOrderId());
        lqw.eq(bo.getOrderType() != null, InventoryHistory::getOrderType, bo.getOrderType());
        lqw.eq(bo.getSkuId() != null, InventoryHistory::getSkuId, bo.getSkuId());
        lqw.eq(bo.getQuantity() != null, InventoryHistory::getQuantity, bo.getQuantity());
        lqw.eq(bo.getWarehouseId() != null, InventoryHistory::getWarehouseId, bo.getWarehouseId());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(!warehouseScopeIds.isEmpty(), InventoryHistory::getWarehouseId, warehouseScopeIds);
        return lqw;
    }

    /**
     * 新增库存记录
     */
    public void insertByBo(InventoryHistoryBo bo) {
        InventoryHistory add = MapstructUtils.convert(bo, InventoryHistory.class);
        inventoryHistoryMapper.insert(add);
    }

    /**
     * 修改库存记录
     */
    public void updateByBo(InventoryHistoryBo bo) {
        InventoryHistory update = MapstructUtils.convert(bo, InventoryHistory.class);
        inventoryHistoryMapper.updateById(update);
    }

    /**
     * 批量删除库存记录
     */
    public void deleteByIds(Collection<Long> ids) {
        inventoryHistoryMapper.deleteBatchIds(ids);
    }
}
