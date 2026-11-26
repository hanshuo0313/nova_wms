package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.PurchaseOrderBo;
import com.ruoyi.wms.domain.vo.PurchaseOrderDetailVo;
import com.ruoyi.wms.domain.vo.PurchaseOrderVo;
import com.ruoyi.wms.service.PurchaseOrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购单
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/purchaseOrder")
public class PurchaseOrderController extends BaseController {

    private final PurchaseOrderService purchaseOrderService;

    @SaCheckPermission("wms:purchase:list")
    @GetMapping("/list")
    public TableDataInfo<PurchaseOrderVo> list(PurchaseOrderBo bo, PageQuery pageQuery) {
        return purchaseOrderService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("wms:purchase:list")
    @GetMapping("/{id}")
    public R<PurchaseOrderVo> getInfo(@PathVariable Long id) {
        return R.ok(purchaseOrderService.queryById(id));
    }

    @SaCheckPermission("wms:purchase:list")
    @GetMapping("/generateNo")
    public R<String> generateNo() {
        R<String> result = R.ok();
        result.setData(purchaseOrderService.generateOrderNo());
        return result;
    }

    @SaCheckPermission("wms:purchase:list")
    @GetMapping("/byNo")
    public R<PurchaseOrderVo> byNo(@RequestParam String orderNo) {
        return R.ok(purchaseOrderService.queryByOrderNo(orderNo));
    }

    @SaCheckPermission("wms:purchase:add")
    @Log(title = "采购单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Long> add(@Validated(AddGroup.class) @RequestBody PurchaseOrderBo bo) {
        return R.ok(purchaseOrderService.insertByBo(bo));
    }

    @SaCheckPermission("wms:purchase:edit")
    @Log(title = "采购单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PurchaseOrderBo bo) {
        purchaseOrderService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 确认采购单并生成采购入库单
     */
    @SaCheckPermission("wms:purchase:edit")
    @Log(title = "采购单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm/{id}")
    public R<Void> confirm(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        purchaseOrderService.confirm(id);
        return R.ok();
    }

    /**
     * 关闭采购单
     */
    @SaCheckPermission("wms:purchase:edit")
    @Log(title = "采购单", businessType = BusinessType.UPDATE)
    @PostMapping("/close/{id}")
    public R<Void> close(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        purchaseOrderService.close(id);
        return R.ok();
    }

    @SaCheckPermission("wms:purchase:remove")
    @Log(title = "采购单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        purchaseOrderService.deleteById(id);
        return R.ok();
    }

    /**
     * 导出采购单明细（Excel）
     */
    @SaCheckPermission("wms:purchase:export")
    @Log(title = "采购单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PurchaseOrderBo bo, HttpServletResponse response) {
        List<PurchaseOrderDetailVo> list = purchaseOrderService.exportList(bo);
        ExcelUtil.exportExcel(list, "采购单", PurchaseOrderDetailVo.class, response);
    }
}
