package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购单视图对象 wms_purchase_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = PurchaseOrder.class)
public class PurchaseOrderVo extends BaseVo {

    @ExcelProperty(value = "ID")
    private Long id;

    @ExcelProperty(value = "采购单号")
    private String orderNo;

    @ExcelProperty(value = "状态")
    private Integer orderStatus;

    @ExcelProperty(value = "入库仓库")
    private Long warehouseId;

    private String warehouseName;

    @ExcelProperty(value = "采购总数量")
    private BigDecimal totalQuantity;

    @ExcelProperty(value = "备注")
    private String remark;

    private List<PurchaseOrderDetailVo> details;
}
