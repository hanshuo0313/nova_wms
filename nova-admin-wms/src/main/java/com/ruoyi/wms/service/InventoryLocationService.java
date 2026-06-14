package com.ruoyi.wms.service;

import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.wms.domain.bo.WarehouseBo;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.vo.WarehouseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.wms.domain.bo.InventoryLocationBo;
import com.ruoyi.wms.domain.vo.InventoryLocationVo;
import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.ruoyi.wms.mapper.InventoryLocationMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 库位管理Service业务层处理
 *
 * @author zcc
 * @date 2026-05-28
 */
@RequiredArgsConstructor
@Service
public class InventoryLocationService {

    private final InventoryLocationMapper inventoryLocationMapper;
    /**
     * 查询库位管理
     */
    public InventoryLocationVo queryById(Long id){
        return inventoryLocationMapper.selectVoById(id);
    }

    /**
     * 查询库位管理列表
     */
    public TableDataInfo<InventoryLocationVo> queryPageList(InventoryLocationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<InventoryLocation> lqw = buildQueryWrapper(bo);
        Page<InventoryLocationVo> result = inventoryLocationMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询库位管理列表
     */
    public List<InventoryLocationVo> queryList(InventoryLocationBo bo) {
        LambdaQueryWrapper<InventoryLocation> lqw = buildQueryWrapper(bo);
        return inventoryLocationMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<InventoryLocation> buildQueryWrapper(InventoryLocationBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<InventoryLocation> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getWarehouseId() != null, InventoryLocation::getWarehouseId, bo.getWarehouseId());
        lqw.eq(StringUtils.isNotBlank(bo.getStockCode()), InventoryLocation::getStockCode, bo.getStockCode());
        lqw.eq(bo.getStatus() != null, InventoryLocation::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增库位管理
     */
    public void insertByBo(InventoryLocationBo bo) {
        InventoryLocation add = MapstructUtils.convert(bo, InventoryLocation.class);
        inventoryLocationMapper.insert(add);
    }

    /**
     * 修改库位管理
     */
    public void updateByBo(InventoryLocationBo bo) {
        InventoryLocation update = MapstructUtils.convert(bo, InventoryLocation.class);
        inventoryLocationMapper.updateById(update);
    }

    /**
     * 批量删除库位管理
     */
    public void deleteByIds(Collection<Long> ids) {
        inventoryLocationMapper.deleteBatchIds(ids);
    }
}
