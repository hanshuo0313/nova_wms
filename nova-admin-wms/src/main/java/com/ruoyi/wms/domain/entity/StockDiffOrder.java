package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 出库/移库缺货差异单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stock_diff_order")
public class StockDiffOrder extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    /** 2=出库 3=移库 */
    private Integer sourceType;

    private Long sourceId;

    private String sourceNo;

    private Long skuId;

    private String stockCode;

    private BigDecimal quantity;

    private Integer status;

    private String remark;
}
