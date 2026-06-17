package com.ruoyi.wms.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.ruoyi.wms.domain.vo.GlossaryVo;
import com.ruoyi.wms.domain.bo.GlossaryBo;
import com.ruoyi.wms.service.GlossaryService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

/**
 * 术语词典
 *
 * @author zcc
 * @date 2026-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/glossary")
public class GlossaryController extends BaseController {

    private final GlossaryService glossaryService;

    /**
     * 查询术语词典列表
     */
    @SaCheckPermission("wms:glossary:list")
    @GetMapping("/list")
    public TableDataInfo<GlossaryVo> list(GlossaryBo bo, PageQuery pageQuery) {
        return glossaryService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询已启用的术语词典（根据 targetLang 过滤）
     */
    @GetMapping("/enabled")
    public R<Map<String, String>> getEnabled(@RequestParam(defaultValue = "en") String targetLang) {
        GlossaryBo bo = new GlossaryBo();
        bo.setTargetLang(targetLang);
        List<GlossaryVo> list = glossaryService.queryList(bo);
        Map<String, String> map = list.stream()
            .collect(Collectors.toMap(
                GlossaryVo::getSourceText,
                GlossaryVo::getTargetText
            ));
        return R.ok(map);
    }

    /**
     * 导出术语词典列表
     */
    @SaCheckPermission("wms:glossary:export")
    @Log(title = "术语词典", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GlossaryBo bo, HttpServletResponse response) {
        List<GlossaryVo> list = glossaryService.queryList(bo);
        ExcelUtil.exportExcel(list, "术语词典", GlossaryVo.class, response);
    }

    /**
     * 获取术语词典详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("wms:glossary:query")
    @GetMapping("/{id}")
    public R<GlossaryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(glossaryService.queryById(id));
    }

    /**
     * 新增术语词典
     */
    @SaCheckPermission("wms:glossary:add")
    @Log(title = "术语词典", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody GlossaryBo bo) {
        glossaryService.insertByBo(bo);
        return R.ok();
    }

    /**
     * 修改术语词典
     */
    @SaCheckPermission("wms:glossary:edit")
    @Log(title = "术语词典", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody GlossaryBo bo) {
        glossaryService.updateByBo(bo);
        return R.ok();
    }

    /**
     * 删除术语词典
     *
     * @param ids 主键串
     */
    @SaCheckPermission("wms:glossary:remove")
    @Log(title = "术语词典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        glossaryService.deleteByIds(List.of(ids));
        return R.ok();
    }

}
