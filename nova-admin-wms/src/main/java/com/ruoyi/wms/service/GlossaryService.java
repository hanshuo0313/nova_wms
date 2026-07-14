package com.ruoyi.wms.service;

import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.wms.domain.bo.GlossaryBo;
import com.ruoyi.wms.domain.vo.GlossaryVo;
import com.ruoyi.wms.domain.entity.Glossary;
import com.ruoyi.wms.mapper.GlossaryMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 术语词典Service业务层处理
 *
 * @author zcc
 * @date 2026-06-17
 */
@RequiredArgsConstructor
@Service
public class GlossaryService {

    private final GlossaryMapper glossaryMapper;

    /**
     * 查询术语词典
     */
    public GlossaryVo queryById(Long id){
        return glossaryMapper.selectVoById(id);
    }

    /**
     * 查询术语词典列表
     */
    public TableDataInfo<GlossaryVo> queryPageList(GlossaryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Glossary> lqw = buildQueryWrapper(bo);
        Page<GlossaryVo> result = glossaryMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询术语词典列表
     */
    public List<GlossaryVo> queryList(GlossaryBo bo) {
        LambdaQueryWrapper<Glossary> lqw = buildQueryWrapper(bo);
        return glossaryMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Glossary> buildQueryWrapper(GlossaryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Glossary> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSourceText()), Glossary::getSourceText, bo.getSourceText());
        lqw.eq(StringUtils.isNotBlank(bo.getTargetText()), Glossary::getTargetText, bo.getTargetText());
        lqw.eq(StringUtils.isNotBlank(bo.getTargetLang()), Glossary::getTargetLang, bo.getTargetLang());
        return lqw;
    }

    /**
     * 新增术语词典
     */
    public void insertByBo(GlossaryBo bo) {
        // 校验同一源文本和同一目标语言下是否已存在
        boolean exists = glossaryMapper.exists(
            Wrappers.<Glossary>lambdaQuery()
                .eq(Glossary::getSourceText, bo.getSourceText())
                .eq(Glossary::getTargetLang, bo.getTargetLang())
        );
        if (exists) {
            throw new ServiceException("术语词典中已存在【" + bo.getSourceText() + "】（" + bo.getTargetLang() + "），不允许重复添加");
        }
        Glossary add = MapstructUtils.convert(bo, Glossary.class);
        glossaryMapper.insert(add);
    }

    /**
     * 修改术语词典
     */
    public void updateByBo(GlossaryBo bo) {
        Glossary update = MapstructUtils.convert(bo, Glossary.class);
        glossaryMapper.updateById(update);
    }

    /**
     * 批量删除术语词典
     */
    public void deleteByIds(Collection<Long> ids) {
        glossaryMapper.deleteBatchIds(ids);
    }
}
