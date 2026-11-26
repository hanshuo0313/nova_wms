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
 * 退货单明细对象 wms_return_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_return_order_detail")
public class ReturnOrderDetail extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;

    /**
     * 采购单明细ID
     */
    private Long purchaseDetailId;

    private Long skuId;

    /**
     * 退货数量
     */
    private BigDecimal returnQuantity;

    /**
     * 采购单实收数量（快照）
     */
    private BigDecimal receivedQuantity;

    /**
     * 可退数量（快照）
     */
    private BigDecimal maxQuantity;

    private String remark;
}
