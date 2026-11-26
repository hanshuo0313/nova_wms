package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_item")
public class Item extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id",  type = IdType.AUTO)
    private Long id;

    /**
     * 编号
     */
    private String itemCode;

    /**
     * 名称
     */
    private String itemName;

    /**
     * 图片
     */
    private String itemImage;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 分类
     */
    private String itemCategory;

    /**
     * 单位类别
     */
    private String unit;

    /**
     * 级别
     */
    private String itemLevel;

    /**
     * 品牌
     */
    private Long itemBrand;

    /**
     * 所属仓库
     */
    private Long warehouseId;

     * 型号
     */
    private String itemModel;

    /**
     * 售价(元)
     */
    private BigDecimal sellingPrice;

    /**
     * 交货期(天)
     */
    private Integer deliveryDays;

    /**
     * 安全库存水位
     */
    private BigDecimal safetyStock;


    /**
     * 备注
     */
    private String remark;


}
