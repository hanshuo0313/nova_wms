package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.wms.domain.entity.Inventory;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存视图对象 wms_inventory
 *
 * @author zcc
 * @date 2024-07-19
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Inventory.class)
public class InventoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 规格ID
     */
    @ExcelProperty(value = "规格ID")
    private Long skuId;

    /**
     * 所属仓库
     */
    @ExcelProperty(value = "所属仓库")
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @ExcelProperty(value = "仓库")
    private String warehouseName;

    /**
     * 库位编号
     */
    @ExcelProperty(value = "库位编号")
    private String stockCode;

    /**
     * 库存
     */
    @ExcelProperty(value = "库存")
    private BigDecimal quantity;

    /**
     * 锁定库存
     */
    private BigDecimal lockedQuantity;

    /**
     * 可用库存 = 库存 - 锁定
     */
    private BigDecimal availableQuantity;

    /**
     * 动碰次数
     */
    @ExcelProperty(value = "动碰次数")
    private Integer moveCount;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商")
    private String supplierName;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    private ItemSkuVo itemSku;

    private ItemVo item;

}
