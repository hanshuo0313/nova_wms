package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.MovementOrder;
import com.ruoyi.wms.domain.vo.MovementOrderVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 移库单Mapper接口
 *
 * @author zcc
 * @date 2024-08-09
 */
@Mapper
public interface MovementOrderMapper extends BaseMapperPlus<MovementOrder, MovementOrderVo> {

}
