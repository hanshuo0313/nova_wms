package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.CheckOrderDetail;
import com.ruoyi.wms.domain.vo.CheckOrderDetailVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存盘点单据详情Mapper接口
 *
 * @author zcc
 * @date 2024-08-13
 */
@Mapper
public interface CheckOrderDetailMapper extends BaseMapperPlus<CheckOrderDetail, CheckOrderDetailVo> {

}
