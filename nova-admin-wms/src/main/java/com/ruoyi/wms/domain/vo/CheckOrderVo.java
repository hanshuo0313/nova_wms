package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.CheckOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存盘点单据视图对象 wms_check_order
 *
 * @author zcc
 * @date 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = CheckOrder.class)
public class CheckOrderVo extends BaseOrderVo<CheckOrderDetailVo> {

    /**
     * 盘点方式 1全盘 2动盘 3指定
     */
    @ExcelProperty(value = "盘点方式")
    private Integer checkType;

    /**
     * 指定盘点-供应商
     */
    private Long checkSupplierId;

    /**
     * 指定盘点-物料
     */
    private Long checkItemId;

    /**
     * 指定盘点-货位编码
     */
    private String checkStockCode;

}
