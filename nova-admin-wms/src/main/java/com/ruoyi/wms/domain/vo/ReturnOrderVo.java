package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import com.ruoyi.wms.domain.entity.ReturnOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 退货单视图对象 wms_return_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = ReturnOrder.class)
public class ReturnOrderVo extends BaseVo {

    @ExcelProperty(value = "ID")
    private Long id;

    @ExcelProperty(value = "退货单号")
    private String orderNo;

    private Long purchaseOrderId;

    @ExcelProperty(value = "关联采购单号")
    private String purchaseOrderNo;

    @ExcelProperty(value = "状态")
    private Integer orderStatus;

    private Long warehouseId;

    private String warehouseName;

    @ExcelProperty(value = "退货总数量")
    private BigDecimal totalQuantity;

    @ExcelProperty(value = "备注")
    private String remark;

    private List<ReturnOrderDetailVo> details;
}
