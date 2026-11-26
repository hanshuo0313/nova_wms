package com.ruoyi.wms.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.wms.domain.entity.CheckDiffDetail;
import com.ruoyi.wms.domain.entity.CheckDiffOrder;
import com.ruoyi.wms.mapper.CheckDiffDetailMapper;
import com.ruoyi.wms.mapper.CheckDiffOrderMapper;
import com.ruoyi.wms.service.CheckDiffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 盘点差异单
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/checkDiff")
public class CheckDiffController {

    private final CheckDiffOrderMapper checkDiffOrderMapper;
    private final CheckDiffDetailMapper checkDiffDetailMapper;
    private final CheckDiffService checkDiffService;

    @GetMapping("/list")
    public TableDataInfo<CheckDiffOrder> list(String orderNo, Long checkOrderId, Integer status, PageQuery pageQuery) {
        Page<CheckDiffOrder> page = checkDiffOrderMapper.selectPage(pageQuery.build(), Wrappers.<CheckDiffOrder>lambdaQuery()
            .like(orderNo != null && !orderNo.isEmpty(), CheckDiffOrder::getOrderNo, orderNo)
            .eq(checkOrderId != null, CheckDiffOrder::getCheckOrderId, checkOrderId)
            .eq(status != null, CheckDiffOrder::getStatus, status)
            .orderByDesc(CheckDiffOrder::getId));
        return TableDataInfo.build(page);
    }

    @GetMapping("/{id}")
    public R<java.util.Map<String, Object>> getInfo(@PathVariable Long id) {
        CheckDiffOrder order = checkDiffOrderMapper.selectById(id);
        if (order == null) {
            return R.fail("差异单不存在");
        }
        List<CheckDiffDetail> details = checkDiffDetailMapper.selectList(Wrappers.<CheckDiffDetail>lambdaQuery()
            .eq(CheckDiffDetail::getDiffOrderId, id));
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("order", order);
        data.put("details", details);
        R<java.util.Map<String, Object>> result = R.ok();
        result.setData(data);
        return result;
    }

    @Log(title = "盘点差异单", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable Long id) {
        checkDiffService.cancel(id);
        return R.ok();
    }

    @Log(title = "盘点差异单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm/{id}")
    public R<Void> confirm(@PathVariable Long id) {
        checkDiffService.confirm(id);
        return R.ok();
    }
}
