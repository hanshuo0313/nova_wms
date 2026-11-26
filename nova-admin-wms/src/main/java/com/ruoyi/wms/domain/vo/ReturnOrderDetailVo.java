package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.mybatis.core.domain.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 退货单明细视图对象 wms_return_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
public class ReturnOrderDetailVo extends BaseVo {

    @ExcelProperty(value = "ID")
    private Long id;

    private Long orderId;

    private Long purchaseDetailId;

    private Long skuId;

    private Long itemId;

    @ExcelProperty(value = "物料编码")
    private String itemCode;

    @ExcelProperty(value = "物料名称")
    private String itemName;

    private Long supplierId;

    @ExcelProperty(value = "供应商名称")
    private String supplierName;

    @ExcelProperty(value = "退货数量")
    private BigDecimal returnQuantity;

    @ExcelProperty(value = "实收数量")
    private BigDecimal receivedQuantity;

    @ExcelProperty(value = "可退数量")
    private BigDecimal maxQuantity;

    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 商品规格信息
     */
    private ItemSkuVo itemSku;
}
