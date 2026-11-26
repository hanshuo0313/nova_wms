package com.ruoyi.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.bo.InventoryBo;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.vo.InventoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * 库存Mapper接口
 *
 * @author zcc
 * @date 2024-07-19
 */
@Mapper
public interface InventoryMapper extends BaseMapperPlus<Inventory, InventoryVo> {

    Page<InventoryVo> queryItemBoardList(Page<InventoryVo> page, @Param("bo") InventoryBo bo,
                                         @Param("warehouseScopeIds") Collection<Long> warehouseScopeIds);

    Page<InventoryVo> queryWarehouseBoardList(Page<InventoryVo> page, @Param("bo") InventoryBo bo,
                                              @Param("warehouseScopeIds") Collection<Long> warehouseScopeIds);

    /**
     * 库存查询（含供应商、分类、等级、型号、规格、动碰次数、安全库存）
     */
    Page<InventoryVo> queryPageList(Page<InventoryVo> page, @Param("bo") InventoryBo bo);

    /**
     * 按仓库+SKU汇总库存（PAD 库存查询）
     */
    Page<InventoryVo> selectSummaryPage(Page<InventoryVo> page, @Param("bo") InventoryBo bo);

    /**
     * 安全库存预警：按仓库+SKU汇总库存后与安全库存比较
     */
    Page<InventoryVo> selectSafetyStockPage(Page<InventoryVo> page, @Param("bo") InventoryBo bo);

}
