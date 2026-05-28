package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.entity.ItemCategory;
import com.ruoyi.wms.domain.vo.ItemCategoryVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemCategoryMapper extends BaseMapperPlus<ItemCategory, ItemCategoryVo> {
}
