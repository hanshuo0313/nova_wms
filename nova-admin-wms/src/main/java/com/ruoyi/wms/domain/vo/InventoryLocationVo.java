package com.ruoyi.wms.domain.vo;

import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serializable;
import java.io.Serial;

/**
 * 库位管理视图对象 inventory_location
 *
 * @author zcc
 * @date 2026-05-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = InventoryLocation.class)
public class InventoryLocationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "序号")
    private Long id;

    /**
     * 所属仓库
     */
    @ExcelProperty(value = "所属仓库")
    private String warehouseId;

    /**
     * 库位编号
     */
    @ExcelProperty(value = "库位编号")
    private String stockCode;

    /**
     * 货架号
     */
    @ExcelProperty(value = "货架号")
    private String shelfNo;

    /**
     * 列
     */
    @ExcelProperty(value = "列")
    private String colNo;

    /**
     * 层
     */
    @ExcelProperty(value = "层")
    private String layerNo;

    /**
     * 长(cm)
     */
    private java.math.BigDecimal length;

    /**
     * 宽(cm)
     */
    private java.math.BigDecimal width;

    /**
     * 高(cm)
     */
    private java.math.BigDecimal height;

    /**
     * 容积(cm³)
     */
    private java.math.BigDecimal volume;

    /**
     * 库位状态：1：空闲、2：使用中
     */
    @ExcelProperty(value = "库位状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "wms_location_status")
    private Integer status;


}
