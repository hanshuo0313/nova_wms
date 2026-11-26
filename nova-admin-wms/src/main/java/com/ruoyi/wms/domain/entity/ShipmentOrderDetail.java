package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 鍑哄簱鍗曡鎯呭璞?wms_shipment_order_detail
 *
 * @author zcc
 * @date 2024-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_shipment_order_detail")
public class ShipmentOrderDetail extends BaseOrderDetail {

    /**
     * 鎵€灞炰粨搴?
     */
    private Long warehouseId;

    /**
     * 宸叉嫞閫夋暟閲忥紙鎿嶄綔鏁伴噺锛?
     */
    private java.math.BigDecimal operatedQuantity;

    /**
     * 计划数量
     */
    private java.math.BigDecimal planQuantity;

    /**
     * 閿佸畾搴撳瓨琛孖D
     */
    private Long inventoryId;
}

