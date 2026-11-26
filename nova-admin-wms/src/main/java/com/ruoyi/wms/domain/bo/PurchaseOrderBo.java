package com.ruoyi.wms.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.wms.domain.entity.PurchaseOrder;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购单业务对象 wms_purchase_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PurchaseOrder.class, reverseConvertGenerate = false)
public class PurchaseOrderBo extends BaseEntity {

    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 采购单号（为空时后端自动生成，供安全库存预警一键生成采购单使用）
     */
    private String orderNo;

    private Integer orderStatus;

    private Long warehouseId;

    private BigDecimal totalQuantity;

    private String remark;

    /**
     * 按安全库存预警生成时：仓库
     */
    private Long alertWarehouseId;

    /**
     * 按安全库存预警生成时：仅这些物料
     */
    private List<Long> alertItemIds;

    /**
     * 采购单号前缀
     */
    private String orderNoPrefix;

    private List<PurchaseOrderDetailBo> details;
}
