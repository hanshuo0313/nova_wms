package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.ReturnOrder;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 退货单业务对象 wms_return_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ReturnOrder.class, reverseConvertGenerate = false)
public class ReturnOrderBo extends BaseEntity {

    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    @NotBlank(message = "退货单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderNo;

    @NotNull(message = "关联采购单不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long purchaseOrderId;

    private String purchaseOrderNo;

    private Integer orderStatus;

    private Long warehouseId;

    private BigDecimal totalQuantity;

    private String remark;

    private List<ReturnOrderDetailBo> details;
}
