package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.ruoyi.wms.domain.vo.InventoryLocationVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库位管理Mapper接口
 *
 * @author zcc
 * @date 2026-05-28
 */
@Mapper
public interface InventoryLocationMapper extends BaseMapperPlus<InventoryLocation, InventoryLocationVo> {

}
