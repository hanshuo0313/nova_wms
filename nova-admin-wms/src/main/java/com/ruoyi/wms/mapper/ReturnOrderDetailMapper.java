package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.entity.ReturnOrderDetail;
import com.ruoyi.wms.domain.vo.ReturnOrderDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReturnOrderDetailMapper extends BaseMapperPlus<ReturnOrderDetail, ReturnOrderDetailVo> {

    List<ReturnOrderDetailVo> queryDetailVoList(@Param("orderId") Long orderId);
}
