package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.vo.MerchantVo;
import com.ruoyi.wms.domain.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 往来单位Mapper接口
 *
 * @author zcc
 * @date 2024-07-05
 */
@Mapper
public interface MerchantMapper extends BaseMapperPlus<Merchant, MerchantVo> {

}
