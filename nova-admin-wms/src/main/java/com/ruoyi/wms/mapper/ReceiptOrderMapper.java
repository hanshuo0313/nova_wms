package com.ruoyi.wms.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.wms.domain.entity.ReceiptOrder;
import com.ruoyi.wms.domain.vo.ReceiptOrderVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单Mapper接口
 *
 * @author zcc
 * @date 2024-07-19
 */
@Mapper
public interface ReceiptOrderMapper extends BaseMapperPlus<ReceiptOrder, ReceiptOrderVo> {

}
