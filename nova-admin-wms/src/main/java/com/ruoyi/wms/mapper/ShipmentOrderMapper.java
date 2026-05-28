package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.ShipmentOrder;
import com.ruoyi.wms.domain.vo.ShipmentOrderVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单Mapper接口
 *
 * @author zcc
 * @date 2024-08-01
 */
@Mapper
public interface ShipmentOrderMapper extends BaseMapperPlus<ShipmentOrder, ShipmentOrderVo> {

}
