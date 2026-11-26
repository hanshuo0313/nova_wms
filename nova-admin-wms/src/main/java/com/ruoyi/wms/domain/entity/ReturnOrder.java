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
 * 退货单对象 wms_return_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_return_order")
public class ReturnOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 退货单号
     */
    private String orderNo;

    /**
     * 关联采购单ID
     */
    private Long purchaseOrderId;

    /**
     * 关联采购单号
     */
    private String purchaseOrderNo;

    /**
     * 0暂存 1已完成 -1作废
     */
    private Integer orderStatus;

    /**
     * 退货仓库
     */
    private Long warehouseId;

    /**
     * 退货总数量
     */
    private BigDecimal totalQuantity;

    private String remark;
}
