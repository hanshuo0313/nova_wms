package com.ruoyi.wms.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.ruoyi.wms.domain.entity.ShipmentOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 鍑哄簱鍗曡鎯呰鍥惧璞?wms_shipment_order_detail
 *
 * @author zcc
 * @date 2024-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ExcelIgnoreUnannotated
@AutoMapper(target = ShipmentOrderDetail.class)
public class ShipmentOrderDetailVo extends BaseOrderDetailVo{

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
