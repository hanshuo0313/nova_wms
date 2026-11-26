package com.ruoyi.wms.service;

import com.ruoyi.wms.domain.entity.StockDiffOrder;
import com.ruoyi.wms.mapper.StockDiffOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 出库/移库缺货差异
 */
@RequiredArgsConstructor
@Service
public class StockDiffService {

    private final StockDiffOrderMapper stockDiffOrderMapper;

    public void create(Integer sourceType, Long sourceId, String sourceNo, Long skuId, String stockCode, BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) {
            return;
        }
        StockDiffOrder order = new StockDiffOrder();
        order.setOrderNo("CY" + (sourceNo == null ? sourceId : sourceNo));
        order.setSourceType(sourceType);
        order.setSourceId(sourceId);
        order.setSourceNo(sourceNo);
        order.setSkuId(skuId);
        order.setStockCode(stockCode);
        order.setQuantity(quantity);
        order.setStatus(0);
        stockDiffOrderMapper.insert(order);
    }
}
