package com.ruoyi.wms.domain.bo;

import com.ruoyi.wms.domain.entity.Glossary;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import io.github.linpeilie.annotations.AutoMapper;


/**
 * 术语词典业务对象 wms_glossary
 *
 * @author zcc
 * @date 2026-06-17
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Glossary.class, reverseConvertGenerate = false)
public class GlossaryBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 原文
     */
    @NotBlank(message = "原文不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sourceText;

    /**
     * 译文
     */
    @NotBlank(message = "译文不能为空", groups = { AddGroup.class, EditGroup.class })
    private String targetText;


    /**
     * 目标语言：en/ja/ko/de...
     */
    @NotBlank(message = "目标语言不能为空", groups = { AddGroup.class, EditGroup.class })
    private String targetLang;


}
