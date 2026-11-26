package com.ruoyi.wms.utils;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.core.domain.bo.LoginUser;
import com.ruoyi.common.satoken.utils.LoginHelper;

import java.util.Collections;
import java.util.List;

/**
 * 仓库数据权限工具类
 * <p>
 * 仓库权限范围由角色配置（sys_role_warehouse）决定：角色未配置仓库时不限制仓库，
 * 当前登录用户拥有全部仓库权限时统一返回空集合。
 * </p>
 *
 * @author zcc
 */
public class WarehouseScopeUtils {

    private WarehouseScopeUtils() {
    }

    /**
     * 获取当前登录用户可访问的仓库ID集合
     *
     * @return 仓库ID集合，空集合表示拥有全部仓库权限
     */
    public static List<Long> getWarehouseIds() {
        if (LoginHelper.isAdmin()) {
            return Collections.emptyList();
        }
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            // 非 web 请求上下文（无登录信息）时不做仓库数据过滤
            return Collections.emptyList();
        }
        if (loginUser == null || CollUtil.isEmpty(loginUser.getWarehouseIds())) {
            return Collections.emptyList();
        }
        return loginUser.getWarehouseIds();
    }

    /**
     * 是否拥有全部仓库权限
     */
    public static boolean isAllScope() {
        return CollUtil.isEmpty(getWarehouseIds());
    }

    /**
     * 校验仓库是否在当前登录用户的权限范围内
     *
     * @param warehouseId 仓库ID
     * @return 是否允许访问
     */
    public static boolean contains(Long warehouseId) {
        if (isAllScope()) {
            return true;
        }
        return warehouseId != null && getWarehouseIds().contains(warehouseId);
    }

}
