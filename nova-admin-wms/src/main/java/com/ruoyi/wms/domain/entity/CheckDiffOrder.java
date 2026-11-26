package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 盘点差异单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_check_diff_order")
public class CheckDiffOrder extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long checkOrderId;

    private String checkOrderNo;

    private Long warehouseId;

    private Integer status;

    private BigDecimal totalDiff;

    private String remark;
}
