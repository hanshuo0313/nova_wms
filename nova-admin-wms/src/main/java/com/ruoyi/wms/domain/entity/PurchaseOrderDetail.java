package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 采购单明细对象 wms_purchase_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_purchase_order_detail")
public class PurchaseOrderDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购单ID
     */
    private Long orderId;

    /**
     * 规格ID
     */
    private Long skuId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 采购数量
     */
    private BigDecimal purchaseQuantity;

    /**
     * 入库数量
     */
    private BigDecimal inboundQuantity;

    /**
     * 退货数量
     */
    private BigDecimal returnQuantity;

    /**
     * 实收数量
     */
    private BigDecimal actualQuantity;

    /**
     * 备注
     */
    private String remark;
}
