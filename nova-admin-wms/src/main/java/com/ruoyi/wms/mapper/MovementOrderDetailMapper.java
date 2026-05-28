package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.MovementOrderDetail;
import com.ruoyi.wms.domain.vo.MovementOrderDetailVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存移动详情Mapper接口
 *
 * @author zcc
 * @date 2024-08-09
 */
@Mapper
public interface MovementOrderDetailMapper extends BaseMapperPlus<MovementOrderDetail, MovementOrderDetailVo> {

}
