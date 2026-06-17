package com.ruoyi.wms.domain.vo;

import com.ruoyi.wms.domain.entity.Glossary;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.excel.annotation.ExcelDictFormat;
import com.ruoyi.common.excel.convert.ExcelDictConvert;
import lombok.Data;
import io.github.linpeilie.annotations.AutoMapper;

import java.io.Serializable;
import java.io.Serial;

/**
 * 术语词典视图对象 wms_glossary
 *
 * @author zcc
 * @date 2026-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Glossary.class)
public class GlossaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 原文
     */
    @ExcelProperty(value = "原文")
    private String sourceText;

    /**
     * 译文
     */
    @ExcelProperty(value = "译文")
    private String targetText;

    /**
     * 目标语言：en/ja/ko/de...
     */
    @ExcelProperty(value = "目标语言", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "language_type")
    private String targetLang;


}
