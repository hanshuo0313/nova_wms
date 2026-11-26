package com.ruoyi.wms.service;

import com.ruoyi.common.core.exception.ServiceException;
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
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import cn.hutool.core.collection.CollUtil;

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
        lqw.like(StringUtils.isNotBlank(bo.getStockCode()), InventoryLocation::getStockCode, bo.getStockCode());
        lqw.eq(StringUtils.isNotBlank(bo.getShelfNo()), InventoryLocation::getShelfNo, bo.getShelfNo());
        lqw.eq(StringUtils.isNotBlank(bo.getColNo()), InventoryLocation::getColNo, bo.getColNo());
        lqw.eq(StringUtils.isNotBlank(bo.getLayerNo()), InventoryLocation::getLayerNo, bo.getLayerNo());
        lqw.eq(bo.getStatus() != null, InventoryLocation::getStatus, bo.getStatus());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(CollUtil.isNotEmpty(warehouseScopeIds), InventoryLocation::getWarehouseId, warehouseScopeIds);
        lqw.orderByAsc(InventoryLocation::getStockCode);
        return lqw;
    }

    /**
     * 依据库位编码解析 货架-列-层（如 CS01-02-03 → cs01 / 02 / 03），并计算容积
     */
    private void normalize(InventoryLocation location) {
        String code = location.getStockCode();
        if (StringUtils.isNotBlank(code)) {
            String[] parts = code.split("-");
            if (StringUtils.isBlank(location.getShelfNo()) && parts.length > 0) {
                location.setShelfNo(parts[0]);
            }
            if (StringUtils.isBlank(location.getColNo()) && parts.length > 1) {
                location.setColNo(parts[1]);
            }
            if (StringUtils.isBlank(location.getLayerNo()) && parts.length > 2) {
                location.setLayerNo(parts[2]);
            }
        }
        if (location.getLength() != null && location.getWidth() != null && location.getHeight() != null) {
            location.setVolume(location.getLength()
                .multiply(location.getWidth())
                .multiply(location.getHeight())
                .setScale(2, java.math.RoundingMode.HALF_UP));
        }
    }

    /**
     * 入库推荐库位：空闲库位中优先容积最小且能容纳物料体积的库位
     */
    public List<InventoryLocationVo> recommend(Long warehouseId, java.math.BigDecimal volume, Integer limit) {
        LambdaQueryWrapper<InventoryLocation> lqw = Wrappers.lambdaQuery();
        lqw.eq(warehouseId != null, InventoryLocation::getWarehouseId, warehouseId);
        lqw.eq(InventoryLocation::getStatus, 1);
        if (volume != null && volume.signum() > 0) {
            lqw.and(w -> w.isNull(InventoryLocation::getVolume).or().ge(InventoryLocation::getVolume, volume));
        }
        lqw.orderByAsc(InventoryLocation::getVolume);
        lqw.orderByAsc(InventoryLocation::getStockCode);
        lqw.last("limit " + (limit == null || limit <= 0 ? 20 : limit));
        return inventoryLocationMapper.selectVoList(lqw);
    }

    /**
     * 新增库位管理
     */
    public void insertByBo(InventoryLocationBo bo) {
        // 校验同一仓库下库位编号是否已存在
        boolean exists = inventoryLocationMapper.exists(
            Wrappers.<InventoryLocation>lambdaQuery()
                .eq(InventoryLocation::getWarehouseId, bo.getWarehouseId())
                .eq(InventoryLocation::getStockCode, bo.getStockCode())
        );
        if (exists) {
            throw new ServiceException("该仓库下库位编号【" + bo.getStockCode() + "】已存在，不允许重复添加");
        }
        InventoryLocation add = MapstructUtils.convert(bo, InventoryLocation.class);
        normalize(add);
        inventoryLocationMapper.insert(add);
    }

    /**
     * 修改库位管理
     */
    public void updateByBo(InventoryLocationBo bo) {
        InventoryLocation update = MapstructUtils.convert(bo, InventoryLocation.class);
        normalize(update);
        inventoryLocationMapper.updateById(update);
    }

    /**
     * 批量删除库位管理
     */
    public void deleteByIds(Collection<Long> ids) {
        inventoryLocationMapper.deleteBatchIds(ids);
    }
}
