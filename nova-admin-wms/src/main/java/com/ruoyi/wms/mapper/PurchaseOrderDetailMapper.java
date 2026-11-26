package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.entity.PurchaseOrderDetail;
import com.ruoyi.wms.domain.vo.PurchaseOrderDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderDetailMapper extends BaseMapperPlus<PurchaseOrderDetail, PurchaseOrderDetailVo> {

    /**
     * 查询采购单明细（含物料、供应商名称）
     */
    List<PurchaseOrderDetailVo> queryDetailVoList(@Param("orderId") Long orderId);
}
