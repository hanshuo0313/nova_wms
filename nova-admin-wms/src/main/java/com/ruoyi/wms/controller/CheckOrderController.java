package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.constant.ServiceConstants;
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
import com.ruoyi.wms.domain.bo.CheckOrderBo;
import com.ruoyi.wms.domain.vo.CheckOrderVo;
import com.ruoyi.wms.service.CheckOrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存盘点单据
 *
 * @author zcc
 * @date 2024-08-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/checkOrder")
public class CheckOrderController extends BaseController {

    private final CheckOrderService checkOrderService;

    /**
     * 查询库存盘点单据列表
     */
    @SaCheckPermission("wms:check:all")
    @GetMapping("/list")
    public TableDataInfo<CheckOrderVo> list(CheckOrderBo bo, PageQuery pageQuery) {
        return checkOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出库存盘点单据列表
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(CheckOrderBo bo, HttpServletResponse response) {
        List<CheckOrderVo> list = checkOrderService.queryList(bo);
        ExcelUtil.exportExcel(list, "库存盘点单据", CheckOrderVo.class, response);
    }

    /**
     * 获取库存盘点单据详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:check:all")
    @GetMapping("/{id}")
    public R<CheckOrderVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(checkOrderService.queryById(id));
    }

    /**
     * 新增库存盘点单据
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody CheckOrderBo bo) {
        bo.setOrderStatus(ServiceConstants.CheckOrderStatus.PENDING);
        checkOrderService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改库存盘点单据
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody CheckOrderBo bo) {
        checkOrderService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 按盘点方式生成盘点明细（全盘/动盘/指定）
     */
    @SaCheckPermission("wms:check:all")
    @PostMapping("/generate")
    public R<java.util.List<com.ruoyi.wms.domain.bo.CheckOrderDetailBo>> generate(@RequestBody CheckOrderBo bo) {
        return R.ok(checkOrderService.generateDetails(bo));
    }

    /**
     * PAD 端逐项盘点确认
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.UPDATE)
    @PostMapping("/padConfirm")
    public R<Void> padConfirm(@RequestBody java.util.Map<String, Object> body) {
        Long orderId = body.get("orderId") == null ? null : Long.valueOf(body.get("orderId").toString());
        Long detailId = body.get("detailId") == null ? null : Long.valueOf(body.get("detailId").toString());
        java.math.BigDecimal checkQuantity = body.get("checkQuantity") == null ? null
            : new java.math.BigDecimal(body.get("checkQuantity").toString());
        if (orderId == null || detailId == null || checkQuantity == null) {
            return R.fail("参数不完整");
        }
        checkOrderService.padConfirm(orderId, detailId, checkQuantity);
        return R.ok();
    }

    /**
     * 取消盘点单（释放锁定库存）
     */
    @SaCheckPermission("wms:check:edit")
    @Log(title = "库存盘点单据", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        checkOrderService.cancel(id);
        return R.ok();
    }

    /**
     * 盘库结束
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/check")
    public R<Void> check(@Validated(AddGroup.class) @RequestBody CheckOrderBo bo) {
        bo.setOrderStatus(ServiceConstants.CheckOrderStatus.FINISH);
        checkOrderService.check(bo);
        return R.ok();
    }

    /**
     * 删除库存盘点单据
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:check:all")
    @Log(title = "库存盘点单据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@NotNull(message = "主键不能为空")
                          @PathVariable Long id) {
        checkOrderService.deleteById(id);
        return R.ok();
    }
}
