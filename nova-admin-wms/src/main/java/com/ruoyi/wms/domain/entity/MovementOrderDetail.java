package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 搴撳瓨绉诲姩璇︽儏瀵硅薄 wms_movement_order_detail
 *
 * @author zcc
 * @date 2024-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_movement_order_detail")
public class MovementOrderDetail extends BaseOrderDetail {

    /**
     * 婧愪粨搴?
     */
    private Long sourceWarehouseId;
    /**
     * 鐩爣浠撳簱
     */
    private Long targetWarehouseId;

    /**
     * 宸叉嫞閫夋暟閲?
     */
    private java.math.BigDecimal operatedQuantity;

    /**
     * 1出库 2入库
     */
    private Integer direction;

    /**
     * 计划数量
     */
    private java.math.BigDecimal planQuantity;

    /**
     * 锁定库存行ID
     */
    private Long inventoryId;

    /**
     * 宸蹭笂鏋舵暟閲?
     */
    private java.math.BigDecimal putawayQuantity;

    /**
     * 鐩爣璐т綅
     */
    private String targetStockCode;

}
