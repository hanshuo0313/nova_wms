package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;


import java.io.Serial;

/**
 * 库位管理对象 inventory_location
 *
 * @author zcc
 * @date 2026-05-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_location")
public class InventoryLocation extends BaseEntity {

    @Serial
    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属仓库
     */
    private Long warehouseId;
    /**
     * 库位编号
     */
    private String stockCode;

    /**
     * 货架号
     */
    private String shelfNo;

    /**
     * 列
     */
    private String colNo;

    /**
     * 层
     */
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
    private Integer status;

}
