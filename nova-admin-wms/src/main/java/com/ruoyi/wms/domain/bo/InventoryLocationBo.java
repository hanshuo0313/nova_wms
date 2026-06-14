package com.ruoyi.wms.domain.bo;

import com.ruoyi.wms.domain.entity.InventoryLocation;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import io.github.linpeilie.annotations.AutoMapper;


/**
 * 库位管理业务对象 inventory_location
 *
 * @author zcc
 * @date 2026-05-28
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = InventoryLocation.class, reverseConvertGenerate = false)
public class InventoryLocationBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 所属仓库
     */
    @NotNull(message = "所属仓库不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long warehouseId;

    /**
     * 库位编号
     */
    @NotBlank(message = "库位编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String stockCode;

    /**
     * 库位状态：1：空闲、2：使用中
     */
    @NotNull(message = "库位状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;


}
