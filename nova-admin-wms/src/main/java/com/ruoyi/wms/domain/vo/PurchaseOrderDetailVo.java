package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购单明细视图对象 wms_purchase_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
public class PurchaseOrderDetailVo extends BaseVo {

    @ExcelProperty(value = "ID")
    private Long id;

    private Long orderId;

    private Long skuId;

    private Long itemId;

    @ExcelProperty(value = "物料编码")
    private String itemCode;

    @ExcelProperty(value = "物料名称")
    private String itemName;

    private Long supplierId;

    @ExcelProperty(value = "供应商名称")
    private String supplierName;

    @ExcelProperty(value = "采购数量")
    private BigDecimal purchaseQuantity;

    @ExcelProperty(value = "入库数量")
    private BigDecimal inboundQuantity;

    @ExcelProperty(value = "退货数量")
    private BigDecimal returnQuantity;

    @ExcelProperty(value = "实收数量")
    private BigDecimal actualQuantity;

    @ExcelProperty(value = "备注")
    private String remark;
}
