package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;


import java.io.Serial;

/**
 * 术语词典对象 wms_glossary
 *
 * @author zcc
 * @date 2026-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_glossary")
public class Glossary extends BaseEntity {

    @Serial
    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 原文
     */
    private String sourceText;
    /**
     * 译文
     */
    private String targetText;

    /**
     * 目标语言：en/ja/ko/de...
     */
    private String targetLang;

}
