package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.entity.MovementOrderDetail;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存移动详情业务对象 wms_movement_order_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMappers({
    @AutoMapper(target = MovementOrderDetail.class, reverseConvertGenerate = false),
    @AutoMapper(target = Inventory.class, reverseConvertGenerate = false)
})
public class MovementOrderDetailBo extends BaseOrderDetailBo {

    /** 源仓库 */
    @NotNull(message = "源仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sourceWarehouseId;

    /** 目标仓库 */
    @NotNull(message = "目标仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long targetWarehouseId;

    /** 目标货位 */
    private String targetStockCode;

    /** 1=出库明细 2=入库明细 */
    private Integer direction;

    /** 计划数量 */
    private java.math.BigDecimal planQuantity;

    /** 锁定库存行ID */
    private Long inventoryId;
}
