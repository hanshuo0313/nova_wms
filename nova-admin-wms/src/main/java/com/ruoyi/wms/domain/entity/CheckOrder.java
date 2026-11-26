package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存盘点单据对象 wms_check_order
 *
 * @author zcc
 * @date 2024-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_check_order")
public class CheckOrder extends BaseOrder {

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 盘点方式 1全盘 2动盘 3指定
     */
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
