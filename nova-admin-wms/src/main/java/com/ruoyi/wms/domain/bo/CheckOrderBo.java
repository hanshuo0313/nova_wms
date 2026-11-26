package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.wms.domain.entity.CheckOrder;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存盘点单据业务对象 wms_check_order
 *
 * @author zcc
 * @date 2024-08-13
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = CheckOrder.class, reverseConvertGenerate = false)
public class CheckOrderBo extends BaseOrderBo<CheckOrderDetailBo> {
    /**
     * 仓库id
     */
    @NotNull(message = "仓库不能为空", groups = { AddGroup.class, EditGroup.class })
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
