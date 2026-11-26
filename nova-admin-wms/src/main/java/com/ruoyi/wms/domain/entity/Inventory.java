package com.ruoyi.wms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 库存对象 wms_inventory
 *
 * @author zcc
 * @date 2024-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inventory")
public class Inventory extends BaseEntity{

    @Serial
    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 规格ID
     */
    private Long skuId;
    /**
     * 所属仓库
     */
    private Long warehouseId;
    /**
     * 库存
     */
    private BigDecimal quantity;

    /**
     * 锁定库存数量
     */
    private BigDecimal lockedQuantity;
    /**
     * 库位编号
     */
    private String stockCode;

    /**
     * 动碰次数（出入库、移库 +1，盘点后清零）
     */
    private Integer moveCount;
    /**
     * 备注
     */
    private String remark;

}
