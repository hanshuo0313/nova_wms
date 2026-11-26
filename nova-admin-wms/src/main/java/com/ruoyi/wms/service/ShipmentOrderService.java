package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.ShipmentOrderBo;
import com.ruoyi.wms.domain.bo.ShipmentOrderDetailBo;
import com.ruoyi.wms.domain.entity.ReceiptOrder;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.entity.ShipmentOrder;
import com.ruoyi.wms.domain.entity.ShipmentOrderDetail;
import com.ruoyi.wms.domain.vo.ReceiptOrderDetailVo;
import com.ruoyi.wms.domain.vo.ReceiptOrderVo;
import com.ruoyi.wms.domain.vo.ShipmentOrderDetailVo;
import com.ruoyi.wms.domain.vo.ShipmentOrderVo;
import com.ruoyi.wms.mapper.ShipmentOrderMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import com.ruoyi.wms.mapper.ShipmentOrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 出库单Service业务层处理
 *
 * @author zcc
 * @date 2024-08-01
 */
@RequiredArgsConstructor
@Service
public class ShipmentOrderService {

    private final ShipmentOrderMapper shipmentOrderMapper;
    private final ShipmentOrderDetailMapper shipmentOrderDetailMapper;
    private final ShipmentOrderDetailService shipmentOrderDetailService;
    private final StockDiffService stockDiffService;
    private final InventoryService inventoryService;
    private final InventoryHistoryService inventoryHistoryService;

    /**
     * 退货出库完成后回写退货单（延迟注入避免循环依赖）
     */
    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private ReturnOrderService returnOrderService;

    /**
     * 查询出库单
     */
    public ShipmentOrderVo queryById(Long id){
        ShipmentOrderVo shipmentOrderVo = shipmentOrderMapper.selectVoById(id);
        if (shipmentOrderVo == null) {
            throw new BaseException("出库单不存在");
        }
        shipmentOrderVo.setDetails(shipmentOrderDetailService.queryByShipmentOrderId(shipmentOrderVo.getId()));
        return shipmentOrderVo;
    }

    /**
     * 查询出库单列表
     */
    public TableDataInfo<ShipmentOrderVo> queryPageList(ShipmentOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShipmentOrder> lqw = buildQueryWrapper(bo);
        Page<ShipmentOrderVo> result = shipmentOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询出库单列表
     */
    public List<ShipmentOrderVo> queryList(ShipmentOrderBo bo) {
        LambdaQueryWrapper<ShipmentOrder> lqw = buildQueryWrapper(bo);
        return shipmentOrderMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShipmentOrder> buildQueryWrapper(ShipmentOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShipmentOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), ShipmentOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOptType() != null, ShipmentOrder::getOptType, bo.getOptType());
        lqw.eq(bo.getShipmentType() != null, ShipmentOrder::getShipmentType, bo.getShipmentType());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), ShipmentOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getMerchantId() != null, ShipmentOrder::getMerchantId, bo.getMerchantId());
        lqw.eq(bo.getTotalAmount() != null, ShipmentOrder::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getTotalQuantity() != null, ShipmentOrder::getTotalQuantity, bo.getTotalQuantity());
        lqw.eq(bo.getOrderStatus() != null, ShipmentOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getWarehouseId() != null, ShipmentOrder::getWarehouseId, bo.getWarehouseId());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(CollUtil.isNotEmpty(warehouseScopeIds), ShipmentOrder::getWarehouseId, warehouseScopeIds);
        lqw.orderByDesc(BaseEntity::getCreateTime);
        return lqw;
    }

    /**
     * 暂存出库单
     */
    @Transactional
    public Long insertByBo(ShipmentOrderBo bo) {
        // 校验出库单号唯一性
        validateShipmentOrderNo(bo.getOrderNo());
        // 创建出库单
        ShipmentOrder add = MapstructUtils.convert(bo, ShipmentOrder.class);
        if (add.getOrderStatus() == null) {
            add.setOrderStatus(ServiceConstants.ShipmentOrderStatus.DRAFT);
        }
        add.setTotalQuantity(sumQuantity(bo.getDetails()));
        shipmentOrderMapper.insert(add);
        bo.setId(add.getId());
        List<ShipmentOrderDetailBo> detailBoList = bo.getDetails();
        List<ShipmentOrderDetail> addDetailList = MapstructUtils.convert(detailBoList, ShipmentOrderDetail.class);
        addDetailList.forEach(it -> it.setOrderId(add.getId()));
        shipmentOrderDetailService.saveDetails(addDetailList);
        return add.getId();
    }

    private BigDecimal sumQuantity(List<ShipmentOrderDetailBo> details) {
        if (CollUtil.isEmpty(details)) {
            return BigDecimal.ZERO;
        }
        return details.stream()
            .map(d -> d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 确认出库单：按当前单据明细配货，锁定库存并生成按货位拆分的出库明细
     */
    @Transactional
    public void confirm(Long id) {
        ShipmentOrderVo vo = queryById(id);
        if (vo == null) {
            throw new BaseException("出库单不存在");
        }
        if (ServiceConstants.ShipmentOrderStatus.FINISH.equals(vo.getOrderStatus())) {
            throw new BaseException("出库单已完成，不能重复确认");
        }
        List<ShipmentOrderDetailVo> details = vo.getDetails();
        if (CollUtil.isEmpty(details)) {
            throw new BaseException("请先添加商品明细");
        }
        // 释放旧的锁定（重新配货）
        for (ShipmentOrderDetailVo detail : details) {
            if (detail.getInventoryId() != null) {
                inventoryService.releaseLock(detail.getInventoryId(), detail.getQuantity());
            }
        }
        List<ShipmentOrderDetail> lockedRows = new LinkedList<>();
        for (ShipmentOrderDetailVo detail : details) {
            BigDecimal plan = detail.getQuantity() == null ? BigDecimal.ZERO : detail.getQuantity();
            BigDecimal available = inventoryService.availableQuantity(vo.getWarehouseId(), detail.getSkuId());
            BigDecimal allocatable = available.min(plan);
            if (allocatable.signum() > 0) {
                List<InventoryService.Allocation> allocations =
                    inventoryService.allocate(vo.getWarehouseId(), detail.getSkuId(), allocatable);
                for (InventoryService.Allocation allocation : allocations) {
                    ShipmentOrderDetail row = new ShipmentOrderDetail();
                    row.setOrderId(id);
                    row.setSkuId(detail.getSkuId());
                    row.setWarehouseId(vo.getWarehouseId());
                    row.setQuantity(allocation.getQuantity());
                    row.setPlanQuantity(allocation.getQuantity());
                    row.setStockCode(allocation.getStockCode());
                    row.setInventoryId(allocation.getInventoryId());
                    lockedRows.add(row);
                }
            }
            // 库存不足时允许部分配货，未配到的数量生成差异单
            BigDecimal residual = plan.subtract(allocatable);
            if (residual.signum() > 0 && vo.getOrderNo() != null) {
                stockDiffService.create(2, id, vo.getOrderNo(), detail.getSkuId(), null, residual);
            }
        }
        shipmentOrderDetailMapper.delete(Wrappers.<ShipmentOrderDetail>lambdaQuery()
            .eq(ShipmentOrderDetail::getOrderId, id));
        lockedRows.forEach(shipmentOrderDetailMapper::insert);
        BigDecimal total = lockedRows.stream()
            .map(d -> d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        shipmentOrderMapper.update(null, Wrappers.<ShipmentOrder>lambdaUpdate()
            .eq(ShipmentOrder::getId, id)
            .set(ShipmentOrder::getOrderStatus, ServiceConstants.ShipmentOrderStatus.PENDING)
            .set(ShipmentOrder::getTotalQuantity, total));
    }

    /**
     * 取消出库单：释放未出库部分的锁定库存
     */
    @Transactional
    public void cancel(Long id) {
        ShipmentOrderVo vo = queryById(id);
        if (vo == null) {
            throw new BaseException("出库单不存在");
        }
        if (ServiceConstants.ShipmentOrderStatus.FINISH.equals(vo.getOrderStatus())) {
            throw new BaseException("出库单已完成，不能取消");
        }
        for (ShipmentOrderDetailVo detail : vo.getDetails()) {
            if (detail.getInventoryId() != null) {
                BigDecimal operated = detail.getOperatedQuantity() == null ? BigDecimal.ZERO : detail.getOperatedQuantity();
                BigDecimal remain = detail.getQuantity().subtract(operated);
                inventoryService.releaseLock(detail.getInventoryId(), remain);
            }
        }
        shipmentOrderMapper.update(null, Wrappers.<ShipmentOrder>lambdaUpdate()
            .eq(ShipmentOrder::getId, id)
            .set(ShipmentOrder::getOrderStatus, ServiceConstants.ShipmentOrderStatus.INVALID));
    }

    public void validateShipmentOrderNo(String shipmentOrderNo) {        LambdaQueryWrapper<ShipmentOrder> receiptOrderLqw = Wrappers.lambdaQuery();
        receiptOrderLqw.eq(ShipmentOrder::getOrderNo, shipmentOrderNo);
        ShipmentOrder shipmentOrder = shipmentOrderMapper.selectOne(receiptOrderLqw);
        Assert.isNull(shipmentOrder, "出库单号重复，请手动修改");
    }


    /**
     * 修改出库单
     */
    @Transactional
    public void updateByBo(ShipmentOrderBo bo) {
        // 更新出库单
        ShipmentOrder update = MapstructUtils.convert(bo, ShipmentOrder.class);
        // 总数量只在建单时写入，执行出库时以数据库原值为准
        update.setTotalQuantity(null);
        shipmentOrderMapper.updateById(update);
        // 保存出库单明细
        List<ShipmentOrderDetail> detailList = MapstructUtils.convert(bo.getDetails(), ShipmentOrderDetail.class);

        //需要考虑detail删除
        List<ShipmentOrderDetailVo> dbList = shipmentOrderDetailService.queryByShipmentOrderId(bo.getId());
        Set<Long> ids = detailList.stream().filter(it -> it.getId() != null).map(it -> it.getId()).collect(Collectors.toSet());
        List<ShipmentOrderDetailVo> delList = dbList.stream().filter(it -> !ids.contains(it.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(delList)) {
            shipmentOrderDetailService.deleteByIds(delList.stream().map(it->it.getId()).collect(Collectors.toList()));
        }
        detailList.forEach(it -> it.setOrderId(bo.getId()));
        shipmentOrderDetailService.saveDetails(detailList);
    }

    /**
     * 批量删除出库单
     */
    public void deleteById(Long id) {
        validateIdBeforeDelete(id);
        shipmentOrderMapper.deleteById(id);
    }

    public void validateIdBeforeDelete(Long id) {
        ShipmentOrderVo shipmentOrderVo = queryById(id);
        if (shipmentOrderVo == null) {
            throw new BaseException("出库单不存在");
        }
        if (ServiceConstants.ShipmentOrderStatus.FINISH.equals(shipmentOrderVo.getOrderStatus())) {
            throw new ServiceException("删除失败", HttpStatus.CONFLICT,"出库单【" + shipmentOrderVo.getOrderNo() + "】已出库，无法删除！");
        }
    }

    /**
     * 出库
     * @param bo
     */
    @Transactional
    public void shipment(ShipmentOrderBo bo) {
        // 0. 合并单据原始信息（PAD 执行时可能只回传明细）
        if (bo.getId() != null) {
            ShipmentOrder db = shipmentOrderMapper.selectById(bo.getId());
            if (db != null) {
                if (bo.getShipmentType() == null) {
                    bo.setShipmentType(db.getShipmentType());
                }
                if (bo.getSourceOrderId() == null) {
                    bo.setSourceOrderId(db.getSourceOrderId());
                }
                if (bo.getSourceOrderNo() == null) {
                    bo.setSourceOrderNo(db.getSourceOrderNo());
                }
                if (bo.getWarehouseId() == null) {
                    bo.setWarehouseId(db.getWarehouseId());
                }
            }
        }
        // 1.校验商品明细不能为空！
        validateBeforeShipment(bo);
        // 2. 保存入库单和入库单明细
        if (Objects.isNull(bo.getId())) {
            insertByBo(bo);
        }
        // 已存在单据执行拣选时只累计数量，不修改明细结构
        // 3.更新库存：Inventory表（只扣减本次拣选数量）
        // 3.更新库存：已配货的按锁定库存行扣减，未配货的按货位扣减
        List<ShipmentOrderDetailBo> fallbackDetails = new LinkedList<>();
        for (ShipmentOrderDetailBo detail : bo.getDetails()) {
            Long inventoryId = detail.getInventoryId();
            if (inventoryId == null && detail.getId() != null) {
                ShipmentOrderDetail dbDetail = shipmentOrderDetailMapper.selectById(detail.getId());
                if (dbDetail != null) {
                    inventoryId = dbDetail.getInventoryId();
                }
            }
            if (inventoryId != null) {
                Inventory inventory = inventoryService.consumeLocked(inventoryId, detail.getQuantity());
                detail.setBeforeQuantity(inventory.getQuantity().add(detail.getQuantity()));
                detail.setAfterQuantity(inventory.getQuantity());
            } else {
                fallbackDetails.add(detail);
            }
        }
        if (!fallbackDetails.isEmpty()) {
            inventoryService.subtract(fallbackDetails);
        }

        // 4.创建库存记录
        inventoryHistoryService.saveInventoryHistory(bo,ServiceConstants.InventoryHistoryOrderType.SHIPMENT,false);

        // 5.累计明细已拣选数量，全部拣选完成后自动关闭出库单
        accumulatePicked(bo.getId(), bo.getDetails());

        // 6.退货出库完成：回写退货单与采购单
        if (ServiceConstants.ShipmentType.RETURN.equals(bo.getShipmentType()) && bo.getSourceOrderId() != null) {
            returnOrderService.finalizeByShipment(bo.getSourceOrderId());
        }
    }

    /**
     * 累计明细已拣选数量
     */
    private void accumulatePicked(Long orderId, List<ShipmentOrderDetailBo> details) {
        if (orderId == null || CollUtil.isEmpty(details)) {
            return;
        }
        List<ShipmentOrderDetail> dbDetails = shipmentOrderDetailMapper.selectList(
            Wrappers.<ShipmentOrderDetail>lambdaQuery().eq(ShipmentOrderDetail::getOrderId, orderId));
        for (ShipmentOrderDetailBo detailBo : details) {
            ShipmentOrderDetail matched = null;
            if (detailBo.getId() != null) {
                matched = dbDetails.stream().filter(d -> Objects.equals(d.getId(), detailBo.getId())).findFirst().orElse(null);
            }
            if (matched == null) {
                matched = dbDetails.stream()
                    .filter(d -> Objects.equals(d.getSkuId(), detailBo.getSkuId()))
                    .findFirst().orElse(null);
            }
            if (matched == null) {
                continue;
            }
            BigDecimal before = matched.getOperatedQuantity() == null ? BigDecimal.ZERO : matched.getOperatedQuantity();
            BigDecimal add = detailBo.getQuantity() == null ? BigDecimal.ZERO : detailBo.getQuantity();
            matched.setOperatedQuantity(before.add(add));
            shipmentOrderDetailMapper.updateById(matched);
        }
        refreshOrderStatus(orderId);
    }

    /**
     * 缺货：该明细视为已拣选完成（不扣库存）
     */
    @Transactional
    public void shortage(Long orderId, Long detailId) {
        ShipmentOrderDetail detail = shipmentOrderDetailMapper.selectById(detailId);
        Assert.notNull(detail, "出库明细不存在");
        Assert.isTrue(Objects.equals(detail.getOrderId(), orderId), "出库明细不属于该单据");
        BigDecimal operated = detail.getOperatedQuantity() == null ? BigDecimal.ZERO : detail.getOperatedQuantity();
        BigDecimal shortQty = detail.getQuantity().subtract(operated);
        detail.setOperatedQuantity(detail.getQuantity());
        shipmentOrderDetailMapper.updateById(detail);
        if (shortQty.signum() > 0) {
            // 缺货：冻结该货位库存，重新在其他货位配货，配不到的生成差异单
            inventoryService.lockAll(detail.getInventoryId());
            ShipmentOrder order = shipmentOrderMapper.selectById(orderId);
            Long warehouseId = order == null ? detail.getWarehouseId() : order.getWarehouseId();
            BigDecimal available = inventoryService.availableQuantity(warehouseId, detail.getSkuId());
            BigDecimal reallocate = available.min(shortQty);
            BigDecimal residual = shortQty.subtract(reallocate);
            if (reallocate.signum() > 0) {
                List<InventoryService.Allocation> allocations =
                    inventoryService.allocate(warehouseId, detail.getSkuId(), reallocate);
                for (InventoryService.Allocation allocation : allocations) {
                    ShipmentOrderDetail row = new ShipmentOrderDetail();
                    row.setOrderId(orderId);
                    row.setSkuId(detail.getSkuId());
                    row.setWarehouseId(warehouseId);
                    row.setQuantity(allocation.getQuantity());
                    row.setStockCode(allocation.getStockCode());
                    row.setInventoryId(allocation.getInventoryId());
                    shipmentOrderDetailMapper.insert(row);
                }
                ShipmentOrder update = new ShipmentOrder();
                update.setId(orderId);
                update.setTotalQuantity((order == null || order.getTotalQuantity() == null
                    ? BigDecimal.ZERO : order.getTotalQuantity()).add(reallocate));
                shipmentOrderMapper.updateById(update);
            }
            if (residual.signum() > 0) {
                stockDiffService.create(2, orderId, order == null ? null : order.getOrderNo(),
                    detail.getSkuId(), detail.getStockCode(), residual);
            }
        }
        refreshOrderStatus(orderId);
    }

    /**
     * 根据明细已拣选数量刷新单据状态：全部完成才置为已出库
     */
    private void refreshOrderStatus(Long orderId) {
        List<ShipmentOrderDetail> dbDetails = shipmentOrderDetailMapper.selectList(
            Wrappers.<ShipmentOrderDetail>lambdaQuery().eq(ShipmentOrderDetail::getOrderId, orderId));
        boolean allDone = !dbDetails.isEmpty() && dbDetails.stream().allMatch(d -> {
            BigDecimal operated = d.getOperatedQuantity() == null ? BigDecimal.ZERO : d.getOperatedQuantity();
            BigDecimal plan = d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity();
            return operated.compareTo(plan) >= 0;
        });
        shipmentOrderMapper.update(null, Wrappers.<ShipmentOrder>lambdaUpdate()
            .eq(ShipmentOrder::getId, orderId)
            .set(ShipmentOrder::getOrderStatus, allDone
                ? ServiceConstants.ShipmentOrderStatus.FINISH
                : ServiceConstants.ShipmentOrderStatus.PENDING));
    }


    private void validateBeforeShipment(ShipmentOrderBo bo) {
        if (CollUtil.isEmpty(bo.getDetails())) {
            throw new BaseException("商品明细不能为空！");
        }
    }

    public Long queryIdByOrderNo(String orderNo) {
        ShipmentOrderVo shipmentOrder = shipmentOrderMapper.selectVoOne(new QueryWrapper<ShipmentOrder>().eq("order_no",orderNo));
        return shipmentOrder != null ? shipmentOrder.getId() : null;
    }
}
