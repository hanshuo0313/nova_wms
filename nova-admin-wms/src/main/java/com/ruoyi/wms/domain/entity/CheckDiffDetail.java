package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 盘点差异单明细
 */
@Data
@TableName("wms_check_diff_detail")
public class CheckDiffDetail {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long diffOrderId;

    private Long checkDetailId;

    private Long skuId;

    private Long itemId;

    private Long warehouseId;

    private Long inventoryId;

    private String stockCode;

    private BigDecimal bookQuantity;

    private BigDecimal checkQuantity;

    private BigDecimal diffQuantity;
}
