package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.MovementOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存移动详情视图对象 wms_movement_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = MovementOrderDetail.class)
public class MovementOrderDetailVo extends BaseOrderDetailVo {

    /** 源仓库 */
    @ExcelProperty(value = "源仓库")
    private Long sourceWarehouseId;

    /** 目标仓库 */
    @ExcelProperty(value = "目标仓库")
    private Long targetWarehouseId;

    private BigDecimal remainQuantity;

    /** 已拣选数量 */
    private BigDecimal operatedQuantity;

    /** 已上架数量 */
    private BigDecimal putawayQuantity;

    /** 目标货位 */
    private String targetStockCode;

    /** 1=出库明细 2=入库明细 */
    private Integer direction;

    /** 计划数量 */
    private BigDecimal planQuantity;

    /** 锁定库存行ID */
    private Long inventoryId;
}
