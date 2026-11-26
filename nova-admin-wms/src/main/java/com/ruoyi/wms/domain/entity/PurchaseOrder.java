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
 * 采购单对象 wms_purchase_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_purchase_order")
public class PurchaseOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购单号
     */
    private String orderNo;

    /**
     * 0草稿 1执行中 2已完成 3已关闭
     */
    private Integer orderStatus;

    /**
     * 入库仓库
     */
    private Long warehouseId;

    /**
     * 采购总数量
     */
    private BigDecimal totalQuantity;

    /**
     * 备注
     */
    private String remark;
}
