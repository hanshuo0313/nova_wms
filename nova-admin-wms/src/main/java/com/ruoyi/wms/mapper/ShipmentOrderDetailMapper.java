package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.ShipmentOrderDetail;
import com.ruoyi.wms.domain.vo.ShipmentOrderDetailVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单详情Mapper接口
 *
 * @author zcc
 * @date 2024-08-01
 */
@Mapper
public interface ShipmentOrderDetailMapper extends BaseMapperPlus<ShipmentOrderDetail, ShipmentOrderDetailVo> {

}
