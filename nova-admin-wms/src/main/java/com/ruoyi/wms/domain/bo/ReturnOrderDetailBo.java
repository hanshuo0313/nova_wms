package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.ReturnOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 退货单明细业务对象 wms_return_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ReturnOrderDetail.class, reverseConvertGenerate = false)
public class ReturnOrderDetailBo extends BaseEntity {

    private Long id;

    private Long orderId;

    private Long purchaseDetailId;

    private Long skuId;

    private BigDecimal returnQuantity;

    private BigDecimal receivedQuantity;

    private BigDecimal maxQuantity;

    private String remark;
}
