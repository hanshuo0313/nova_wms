package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import com.ruoyi.wms.domain.vo.PurchaseOrderVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderMapper extends BaseMapperPlus<PurchaseOrder, PurchaseOrderVo> {
}
