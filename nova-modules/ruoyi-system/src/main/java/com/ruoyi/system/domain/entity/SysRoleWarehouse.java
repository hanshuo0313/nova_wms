package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和仓库关联 sys_role_warehouse（仓库数据权限）
 *
 * @author Lion Li
 */

@Data
@TableName("sys_role_warehouse")
public class SysRoleWarehouse {

    /**
     * 角色ID
     */
    @TableId(type = IdType.INPUT)
    private Long roleId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

}
