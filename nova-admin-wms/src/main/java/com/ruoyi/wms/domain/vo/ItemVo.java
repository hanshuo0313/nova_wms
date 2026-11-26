package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.Item;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Item.class)
public class ItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 编号
     */
    @ExcelProperty(value = "物料编码")
    private String itemCode;

    /**
     * 名称
     */
    @ExcelProperty(value = "物料名称")
    private String itemName;

    /**
     * 图片
     */
    @ExcelProperty(value = "图片")
    private String itemImage;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商")
    private String supplierName;

    /**
     * 分类
     */
    @ExcelProperty(value = "分类")
    private String itemCategory;

    /**
     * 单位类别
     */
    @ExcelProperty(value = "单位类别")
    private String unit;

    /**
     * 级别
     */
    @ExcelProperty(value = "等级")
    private String itemLevel;

    /**
     * 品牌
     */
    @ExcelProperty(value = "品牌")
    private Long itemBrand;

    /**
     * 所属仓库
     */
    @ExcelProperty(value = "所属仓库")
    private Long warehouseId;
    
    /**
     * 品牌名称
     */
    @ExcelProperty(value = "品牌名称")
    private String itemBrandName;

    /**
     * 型号
     */
    @ExcelProperty(value = "型号")
    private String itemModel;

    /**
     * 售价(元)
     */
    @ExcelProperty(value = "售价")
    private BigDecimal sellingPrice;

    /**
     * 交货期(天)
     */
    @ExcelProperty(value = "交货期")
    private Integer deliveryDays;

    /**
     * 安全库存水位
     */
    @ExcelProperty(value = "安全库存水位")
    private BigDecimal safetyStock;


    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 类别
     */
    private ItemCategoryVo itemCategoryInfo;

    /**
     * 类别名称（列表展示用）
     */
    private String itemCategoryName;

    /**
     * 主规格（列表展示用，取该物料第一个规格）
     */
    private Long skuId;
    private String skuName;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal volume;
    private BigDecimal grossWeight;
    private BigDecimal netWeight;
    private BigDecimal costPrice;
}
