package com.ruoyi.wms.mapper;

import com.ruoyi.wms.domain.entity.CheckOrder;
import com.ruoyi.wms.domain.vo.CheckOrderVo;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存盘点单据Mapper接口
 *
 * @author zcc
 * @date 2024-08-13
 */
@Mapper
public interface CheckOrderMapper extends BaseMapperPlus<CheckOrder, CheckOrderVo> {

}
