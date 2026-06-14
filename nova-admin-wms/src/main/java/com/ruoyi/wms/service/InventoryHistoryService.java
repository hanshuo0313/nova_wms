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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            InventoryHistory inventoryHistory = new InventoryHistory();
            inventoryHistory.setOrderId(bo.getId());
            inventoryHistory.setOrderNo(bo.getOrderNo());
            inventoryHistory.setOrderType(orderType);
            inventoryHistory.setSkuId(detail.getSkuId());

            LambdaQueryWrapper<Inventory> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(Inventory::getWarehouseId, detail.getWarehouseId());
            wrapper.eq(Inventory::getSkuId, detail.getSkuId());
            Inventory result = inventoryMapper.selectOne(wrapper);
            if(orderType==4){
                inventoryHistory.setStockCode(detail.getStockCode());
            }else{
                if(result!=null){
                    String existingStockCode = result.getStockCode();
                    String newStockCode = detail.getStockCode();
                    if (existingStockCode != null && !existingStockCode.isEmpty() && newStockCode != null && !newStockCode.isEmpty()) {
                        List<String> existingStockCodeList = Arrays.asList(existingStockCode.split(","));
                        List<String> newStockCodeList = Arrays.asList(newStockCode.split(","));
                        Set<String> allCodes = new HashSet<>(existingStockCodeList);
                        allCodes.addAll(newStockCodeList.stream().map(String::trim).toList());
                        inventoryHistory.setStockCode(String.join(",", allCodes));
                    } else {
                        inventoryHistory.setStockCode(newStockCode);
                    }
                }
            }


            if(isAdd){
                inventoryHistory.setQuantity(detail.getQuantity());
            }else {
                inventoryHistory.setQuantity(detail.getQuantity().negate());
            }
            inventoryHistory.setWarehouseId(detail.getWarehouseId());
            inventoryHistory.setAmount(detail.getAmount());
            inventoryHistory.setBeforeQuantity(detail.getBeforeQuantity());
            inventoryHistory.setAfterQuantity(detail.getAfterQuantity());
            inventoryHistoryList.add(inventoryHistory);
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
        Page<InventoryHistoryVo> result = inventoryHistoryMapper.selectVoPageByBo(pageQuery.build(), bo);
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
