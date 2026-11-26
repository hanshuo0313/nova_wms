package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.ReturnOrderBo;
import com.ruoyi.wms.domain.vo.ReturnOrderDetailVo;
import com.ruoyi.wms.domain.vo.ReturnOrderVo;
import com.ruoyi.wms.service.ReturnOrderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 退货单
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/returnOrder")
public class ReturnOrderController extends BaseController {

    private final ReturnOrderService returnOrderService;

    @SaCheckPermission("wms:return:list")
    @GetMapping("/list")
    public TableDataInfo<ReturnOrderVo> list(ReturnOrderBo bo, PageQuery pageQuery) {
        return returnOrderService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("wms:return:list")
    @GetMapping("/{id}")
    public R<ReturnOrderVo> getInfo(@PathVariable Long id) {
        return R.ok(returnOrderService.queryById(id));
    }

    @SaCheckPermission("wms:return:list")
    @GetMapping("/generateNo")
    public R<String> generateNo() {
        R<String> result = R.ok();
        result.setData(returnOrderService.generateOrderNo());
        return result;
    }

    /**
     * 查询采购单可退货明细
     */
    @SaCheckPermission("wms:return:list")
    @GetMapping("/available/{purchaseOrderId}")
    public R<List<ReturnOrderDetailVo>> available(@PathVariable Long purchaseOrderId) {
        return R.ok(returnOrderService.availableDetails(purchaseOrderId));
    }

    @SaCheckPermission("wms:return:add")
    @Log(title = "退货单", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Long> add(@Validated(AddGroup.class) @RequestBody ReturnOrderBo bo) {
        return R.ok(returnOrderService.insertByBo(bo));
    }

    @SaCheckPermission("wms:return:edit")
    @Log(title = "退货单", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ReturnOrderBo bo) {
        returnOrderService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 完成退货（已改为在退货出库单中执行出库后自动完成）
     */
    @SaCheckPermission("wms:return:edit")
    @Log(title = "退货单", businessType = BusinessType.UPDATE)
    @PostMapping("/complete/{id}")
    public R<Void> complete(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.fail("请在退货出库单中执行出库操作，完成后退货单自动结束");
    }

    @SaCheckPermission("wms:return:remove")
    @Log(title = "退货单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        returnOrderService.deleteById(id);
        return R.ok();
    }
}
