package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.PurchaseOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购单明细业务对象 wms_purchase_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PurchaseOrderDetail.class, reverseConvertGenerate = false)
public class PurchaseOrderDetailBo extends BaseEntity {

    private Long id;

    private Long orderId;

    private Long skuId;

    private Long supplierId;

    private BigDecimal purchaseQuantity;

    private BigDecimal inboundQuantity;

    private BigDecimal returnQuantity;

    private BigDecimal actualQuantity;

    private String remark;
}
