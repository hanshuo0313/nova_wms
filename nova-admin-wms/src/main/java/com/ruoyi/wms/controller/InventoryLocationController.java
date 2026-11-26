package com.ruoyi.wms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.wms.domain.entity.Warehouse;
import com.ruoyi.wms.domain.vo.WarehouseVo;
import com.ruoyi.wms.mapper.WarehouseMapper;
import com.ruoyi.wms.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.wms.domain.vo.InventoryLocationVo;
import com.ruoyi.wms.domain.bo.InventoryLocationBo;
import com.ruoyi.wms.service.InventoryLocationService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

/**
 * 库位管理
 *
 * @author zcc
 * @date 2026-05-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/location")
public class InventoryLocationController extends BaseController {

    private final InventoryLocationService inventoryLocationService;
    private final WarehouseMapper warehouseMapper;

    /**
     * 查询库位管理列表（出入库单据/看板等只读引用场景，仅要求登录，并按仓库数据权限过滤）
     */
    @GetMapping("/list")
    public TableDataInfo<InventoryLocationVo> list(InventoryLocationBo bo, PageQuery pageQuery) {
        return inventoryLocationService.queryPageList(bo, pageQuery);
    }

    /**
     * 校验货位是否存在且属于该仓库
     */
    @SaCheckPermission("wms:location:list")
    @GetMapping("/validate")
    public R<Boolean> validateLocation(@RequestParam Long warehouseId, @RequestParam String stockCode) {
        com.ruoyi.wms.domain.bo.InventoryLocationBo query = new com.ruoyi.wms.domain.bo.InventoryLocationBo();
        query.setWarehouseId(warehouseId);
        query.setStockCode(stockCode);
        boolean exists = inventoryLocationService.queryList(query).stream()
            .anyMatch(it -> stockCode.equalsIgnoreCase(it.getStockCode()));
        return R.ok(exists);
    }

    /**
     * 入库推荐库位（按容积匹配）
     */
    @SaCheckPermission("wms:location:list")
    @GetMapping("/recommend")
    public R<List<InventoryLocationVo>> recommend(@RequestParam(required = false) Long warehouseId,
                                                  @RequestParam(required = false) java.math.BigDecimal volume) {
        return R.ok(inventoryLocationService.recommend(warehouseId, volume, 20));
    }

    /**
     * 导出库位管理列表
     */
    /**
     * 导出库位管理列表
     */
    @SaCheckPermission("wms:location:export")
    @Log(title = "库位管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(InventoryLocationBo bo, HttpServletResponse response) {
        List<InventoryLocationVo> list = inventoryLocationService.queryList(bo);
        LambdaQueryWrapper<Warehouse> lqw = Wrappers.lambdaQuery();
        List<Warehouse> warehouseList = warehouseMapper.selectList(lqw);

        // 创建仓库ID到名称的映射
        Map<Long, String> warehouseMap = warehouseList.stream()
            .collect(Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));

        Long index=1L;
        for (InventoryLocationVo vo : list) {
            vo.setId(index++);
            vo.setWarehouseId(warehouseMap.get(Long.valueOf(vo.getWarehouseId())));
        }

        ExcelUtil.exportExcel(list, "库位管理", InventoryLocationVo.class, response);
    }


    /**
     * 获取库位管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:location:query")
    @GetMapping("/{id}")
    public R<InventoryLocationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(inventoryLocationService.queryById(id));
    }

    /**
     * 新增库位管理
     */
    @SaCheckPermission("wms:location:add")
    @Log(title = "库位管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody InventoryLocationBo bo) {
        inventoryLocationService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改库位管理
     */
    @SaCheckPermission("wms:location:edit")
    @Log(title = "库位管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody InventoryLocationBo bo) {
        inventoryLocationService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除库位管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("wms:location:remove")
    @Log(title = "库位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        inventoryLocationService.deleteByIds(List.of(ids));
        return R.ok();
    }
}
