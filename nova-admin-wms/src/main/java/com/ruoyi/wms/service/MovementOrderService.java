package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.MovementOrderBo;
import com.ruoyi.wms.domain.bo.MovementOrderDetailBo;
import com.ruoyi.wms.domain.entity.MovementOrder;
import com.ruoyi.wms.domain.entity.MovementOrderDetail;
import com.ruoyi.wms.domain.vo.MovementOrderVo;
import com.ruoyi.wms.domain.vo.MovementOrderDetailVo;
import com.ruoyi.wms.mapper.MovementOrderMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import com.ruoyi.wms.mapper.MovementOrderDetailMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 移库单Service业务层处理
 *
 * @author zcc
 * @date 2024-08-09
 */
@RequiredArgsConstructor
@Service
public class MovementOrderService {

    private final MovementOrderMapper movementOrderMapper;
    private final MovementOrderDetailMapper movementOrderDetailMapper;
    private final MovementOrderDetailService movementOrderDetailService;
    private final InventoryService inventoryService;
    private final InventoryHistoryService inventoryHistoryService;
    private final StockDiffService stockDiffService;


    /**
     * 查询移库单
     */
    public MovementOrderVo queryById(Long id) {
        MovementOrderVo movementOrderVo = movementOrderMapper.selectVoById(id);
        if (movementOrderVo == null) {
            throw new BaseException("移库单不存在");
        }
        movementOrderVo.setDetails(movementOrderDetailService.queryByMovementOrderId(id));
        return movementOrderVo;
    }

    /**
     * 查询移库单列表
     */
    public TableDataInfo<MovementOrderVo> queryPageList(MovementOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MovementOrder> lqw = buildQueryWrapper(bo);
        Page<MovementOrderVo> result = movementOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询移库单列表
     */
    public List<MovementOrderVo> queryList(MovementOrderBo bo) {
        LambdaQueryWrapper<MovementOrder> lqw = buildQueryWrapper(bo);
        return movementOrderMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MovementOrder> buildQueryWrapper(MovementOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MovementOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), MovementOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getSourceWarehouseId() != null, MovementOrder::getSourceWarehouseId, bo.getSourceWarehouseId());
        lqw.eq(bo.getTargetWarehouseId() != null, MovementOrder::getTargetWarehouseId, bo.getTargetWarehouseId());
        lqw.eq(bo.getOrderStatus() != null, MovementOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getTotalQuantity() != null, MovementOrder::getTotalQuantity, bo.getTotalQuantity());
        // 仓库数据权限过滤（来源仓库或目标仓库在权限范围内）
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.and(!warehouseScopeIds.isEmpty(), w -> w.in(MovementOrder::getSourceWarehouseId, warehouseScopeIds)
            .or().in(MovementOrder::getTargetWarehouseId, warehouseScopeIds));
        lqw.orderByDesc(BaseEntity::getCreateTime);
        return lqw;
    }

    /**
     * 新增移库单
     */
    @Transactional
    public void insertByBo(MovementOrderBo bo) {
        // 1.校验移库单号唯一性
        validateMovementOrderNo(bo.getOrderNo());
        // 2.创建移库单
        MovementOrder add = MapstructUtils.convert(bo, MovementOrder.class);
        movementOrderMapper.insert(add);
        bo.setId(add.getId());
        // 3.创建移库单明细
        List<MovementOrderDetail> addDetailList = MapstructUtils.convert(bo.getDetails(), MovementOrderDetail.class);
        addDetailList.forEach(it -> {
            it.setOrderId(add.getId());
        });
        movementOrderDetailService.saveDetails(addDetailList);
    }

    private void validateMovementOrderNo(String movementOrderNo) {
        LambdaQueryWrapper<MovementOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MovementOrder::getOrderNo, movementOrderNo);
        if (movementOrderMapper.exists(lambdaQueryWrapper)) {
            throw new BaseException("移库单号重复，请手动修改");
        }
    }

    /**
     * 修改移库单
     */
    @Transactional
    public void updateByBo(MovementOrderBo bo) {
        // 1.更新移库单
        MovementOrder update = MapstructUtils.convert(bo, MovementOrder.class);
        movementOrderMapper.updateById(update);
        // 2.保存移库单明细
        movementOrderDetailMapper.delete(Wrappers.<MovementOrderDetail>lambdaQuery()
            .eq(MovementOrderDetail::getOrderId, bo.getId()));
        List<MovementOrderDetail> detailList = MapstructUtils.convert(bo.getDetails(), MovementOrderDetail.class);
        detailList.forEach(it -> it.setOrderId(bo.getId()));
        movementOrderDetailService.saveDetails(detailList);
    }

    /**
     * 删除移库单
     * @param id
     */
    public void deleteById(Long id) {
        validateIdBeforeDelete(id);
        movementOrderMapper.deleteById(id);
    }

    private void validateIdBeforeDelete(Long id) {
        MovementOrderVo movementOrderVo = queryById(id);
        if (movementOrderVo == null) {
            throw new BaseException("移库单不存在");
        }
        if (ServiceConstants.MovementOrderStatus.FINISH.equals(movementOrderVo.getOrderStatus())) {
            throw new ServiceException("移库单【" + movementOrderVo.getOrderNo() + "】已移库，无法删除！");
        }
    }

    /**
     * 批量删除移库单
     */
    public void deleteByIds(Collection<Long> ids) {
        movementOrderMapper.deleteBatchIds(ids);
    }

    /**
     * 移库
     * @param bo
     */
    @Transactional
    public void move(MovementOrderBo bo) {


        // 1.校验商品明细不能为空！
        validateBeforeMove(bo);

        // 3.保存移库单核移库单明细
        if (Objects.isNull(bo.getId())) {
            insertByBo(bo);
        } else {
            updateByBo(bo);
        }
        // 4.更新库存Inventory
        MovementOrderBo shipmentBo = getShipmentBo(bo);
        inventoryService.subtract(shipmentBo.getDetails());

        MovementOrderBo receiptBo = getReceiptBo(bo);
        inventoryService.add(receiptBo.getDetails());


        // 6.创建库存记录流水
        inventoryHistoryService.saveInventoryHistory(shipmentBo, ServiceConstants.InventoryHistoryOrderType.MOVEMENT,false);
        inventoryHistoryService.saveInventoryHistory(receiptBo, ServiceConstants.InventoryHistoryOrderType.MOVEMENT,true);
    }

    private MovementOrderBo getReceiptBo(MovementOrderBo bo) {

        MovementOrderBo receiptBo = SerializationUtils.clone(bo);
        receiptBo.getDetails().forEach(detail -> detail.setWarehouseId(detail.getTargetWarehouseId()));
        return receiptBo;
    }

    private MovementOrderBo getShipmentBo(MovementOrderBo bo) {
        MovementOrderBo shipmentBo = SerializationUtils.clone(bo);
        shipmentBo.getDetails().forEach(detail -> detail.setWarehouseId(detail.getSourceWarehouseId()));
        return shipmentBo;
    }

    private void validateBeforeMove(MovementOrderBo bo) {
        if (CollUtil.isEmpty(bo.getDetails())) {
            throw new BaseException("商品明细不能为空！");
        }
    }

    /**
     * PAD 鎷ｉ€夛紙婧愪粨鍑哄簱锛夛細鎸夋槑缁嗙疮璁″凡鎷ｉ€夋暟閲?
     */
    /**
     * 纭绉诲簱鍗曪細鎸夊彲鐢ㄥ簱瀛橀厤璐э紝鐢熸垚鍑哄簱鏄庣粏涓庡叆搴撴槑缁嗭紙缁戝畾鍚屼竴绉诲簱鍗曪級
     */
    @Transactional
    public void confirm(Long id) {
        MovementOrderVo vo = queryById(id);
        if (vo == null) {
            throw new BaseException("绉诲簱鍗曚笉瀛樺湪");
        }
        if (ServiceConstants.MovementOrderStatus.FINISH.equals(vo.getOrderStatus())) {
            throw new BaseException("绉诲簱鍗曞凡瀹屾垚");
        }
        MovementOrder orderDb = movementOrderMapper.selectById(id);
        if (orderDb == null) {
            throw new BaseException("移库单不存在");
        }
        Long sourceWarehouseId = orderDb.getSourceWarehouseId();
        Long targetWarehouseId = orderDb.getTargetWarehouseId();
        List<MovementOrderDetailVo> details = vo.getDetails();
        if (CollUtil.isEmpty(details)) {
            throw new BaseException("璇峰厛娣诲姞鍟嗗搧鏄庣粏");
        }
        // 鍏堥噴鏀炬棫閿佸畾
        for (MovementOrderDetailVo detail : details) {
            if (detail.getInventoryId() != null) {
                inventoryService.releaseLock(detail.getInventoryId(), detail.getQuantity());
            }
        }
        // 鍒犻櫎鏃ф槑缁嗭紙鑽夌鏄庣粏鎴栦箣鍓嶇殑閰嶈揣缁撴灉锛?
        movementOrderDetailMapper.delete(Wrappers.<MovementOrderDetail>lambdaQuery()
            .eq(MovementOrderDetail::getOrderId, id));
        BigDecimal total = BigDecimal.ZERO;
        for (MovementOrderDetailVo detail : details) {
            BigDecimal plan = detail.getQuantity() == null ? BigDecimal.ZERO : detail.getQuantity();
            BigDecimal available = inventoryService.availableQuantity(sourceWarehouseId, detail.getSkuId());
            BigDecimal allocatable = available.min(plan);
            if (allocatable.signum() > 0) {
                List<InventoryService.Allocation> allocations =
                    inventoryService.allocate(sourceWarehouseId, detail.getSkuId(), allocatable);
                for (InventoryService.Allocation allocation : allocations) {
                    // 鍑哄簱鏄庣粏
                    MovementOrderDetail out = new MovementOrderDetail();
                    out.setOrderId(id);
                    out.setDirection(1);
                    out.setSkuId(detail.getSkuId());
                    out.setQuantity(allocation.getQuantity());
                    out.setPlanQuantity(allocation.getQuantity());
                    out.setSourceWarehouseId(sourceWarehouseId);
                    out.setStockCode(allocation.getStockCode());
                    out.setInventoryId(allocation.getInventoryId());
                    out.setOperatedQuantity(BigDecimal.ZERO);
                    out.setPutawayQuantity(BigDecimal.ZERO);
                    movementOrderDetailMapper.insert(out);
                    // 瀵瑰簲鐨勫叆搴撴槑缁?
                    MovementOrderDetail in = new MovementOrderDetail();
                    in.setOrderId(id);
                    in.setDirection(2);
                    in.setSkuId(detail.getSkuId());
                    in.setQuantity(allocation.getQuantity());
                    in.setPlanQuantity(allocation.getQuantity());
                    in.setTargetWarehouseId(targetWarehouseId);
                    in.setOperatedQuantity(BigDecimal.ZERO);
                    in.setPutawayQuantity(BigDecimal.ZERO);
                    movementOrderDetailMapper.insert(in);
                    total = total.add(allocation.getQuantity());
                }
            }
            BigDecimal residual = plan.subtract(allocatable);
            if (residual.signum() > 0 && vo.getOrderNo() != null) {
                stockDiffService.create(3, id, vo.getOrderNo(), detail.getSkuId(), detail.getStockCode(), residual);
            }
        }
        movementOrderMapper.update(null, Wrappers.<MovementOrder>lambdaUpdate()
            .eq(MovementOrder::getId, id)
            .set(MovementOrder::getOrderStatus, ServiceConstants.MovementOrderStatus.PENDING)
            .set(MovementOrder::getTotalQuantity, total));
    }

    @Transactional
    public void pick(MovementOrderBo bo) {
        validateBeforeMove(bo);
        MovementOrderBo shipmentBo = getShipmentBo(bo);
        inventoryService.subtract(shipmentBo.getDetails());
        inventoryHistoryService.saveInventoryHistory(shipmentBo, ServiceConstants.InventoryHistoryOrderType.MOVEMENT, false);
        accumulate(bo, false);
    }

    /**
     * PAD 涓婃灦锛堢洰鏍囦粨鍏ュ簱锛夛細鎸夋槑缁嗙疮璁″凡涓婃灦鏁伴噺锛屽叏閮ㄥ畬鎴愬悗鍏冲崟
     */
    @Transactional
    public void putaway(MovementOrderBo bo) {
        validateBeforeMove(bo);
        if (bo.getId() != null) {
            List<MovementOrderDetail> outRows = movementOrderDetailMapper.selectList(
                Wrappers.<MovementOrderDetail>lambdaQuery()
                    .eq(MovementOrderDetail::getOrderId, bo.getId())
                    .eq(MovementOrderDetail::getDirection, 1));
            boolean allOutDone = outRows.isEmpty() || outRows.stream().allMatch(d -> {
                BigDecimal operated = d.getOperatedQuantity() == null ? BigDecimal.ZERO : d.getOperatedQuantity();
                BigDecimal plan = d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity();
                return operated.compareTo(plan) >= 0;
            });
            if (!allOutDone) {
                throw new BaseException("璇峰厛瀹屾垚鍏ㄩ儴鍑哄簱鎿嶄綔锛屽啀杩涜鍏ュ簱");
            }
        }
        MovementOrderBo receiptBo = getReceiptBo(bo);
        receiptBo.getDetails().forEach(d -> {
            if (StringUtils.isNotBlank(d.getTargetStockCode())) {
                d.setStockCode(d.getTargetStockCode());
            }
            d.setWarehouseId(d.getTargetWarehouseId());
        });
        inventoryService.add(receiptBo.getDetails());
        inventoryHistoryService.saveInventoryHistory(receiptBo, ServiceConstants.InventoryHistoryOrderType.MOVEMENT, true);
        accumulate(bo, true);
    }

    /**
     * 缂鸿揣锛氳鏄庣粏瑙嗕负宸叉嫞閫夊畬鎴愶紙涓嶆墸搴撳瓨锛?
     */
    @Transactional
    public void shortage(Long orderId, Long detailId) {
        MovementOrderDetail detail = movementOrderDetailMapper.selectById(detailId);
        Assert.notNull(detail, "移库明细不存在");
        BigDecimal operated = detail.getOperatedQuantity() == null ? BigDecimal.ZERO : detail.getOperatedQuantity();
        BigDecimal shortQty = detail.getQuantity().subtract(operated);
        detail.setOperatedQuantity(detail.getQuantity());
        // 缂鸿揣瑙嗕负璇ユ槑缁嗘棤闇€涓婃灦锛岀洿鎺ユ爣璁颁笂鏋跺畬鎴愶紝渚夸簬鏁村崟鍏抽棴
        detail.setPutawayQuantity(detail.getQuantity());
        movementOrderDetailMapper.updateById(detail);
        if (shortQty.signum() > 0) {
            MovementOrder order = movementOrderMapper.selectById(orderId);
            inventoryService.lockLocation(detail.getSourceWarehouseId(), detail.getSkuId(), detail.getStockCode());
            stockDiffService.create(3, orderId, order == null ? null : order.getOrderNo(),
                detail.getSkuId(), detail.getStockCode(), shortQty);
            // 缂鸿揣鏄庣粏鏃犻渶鍏ュ簱锛屽叆搴撴槑缁嗗悓姝ユ爣璁板畬鎴?
            List<MovementOrderDetail> inRows = movementOrderDetailMapper.selectList(
                Wrappers.<MovementOrderDetail>lambdaQuery()
                    .eq(MovementOrderDetail::getOrderId, orderId)
                    .eq(MovementOrderDetail::getDirection, 2)
                    .eq(MovementOrderDetail::getSkuId, detail.getSkuId()));
            for (MovementOrderDetail in : inRows) {
                in.setPutawayQuantity(in.getQuantity());
                movementOrderDetailMapper.updateById(in);
            }
        }
        refreshStatus(orderId);
    }

    private void accumulate(MovementOrderBo bo, boolean putaway) {
        if (bo.getId() == null) {
            return;
        }
        List<MovementOrderDetail> dbDetails = movementOrderDetailMapper.selectList(
            Wrappers.<MovementOrderDetail>lambdaQuery().eq(MovementOrderDetail::getOrderId, bo.getId()));
        dbDetails = dbDetails.stream()
            .filter(d -> putaway
                ? (d.getDirection() != null && d.getDirection() == 2)
                : (d.getDirection() == null || d.getDirection() == 1))
            .toList();
        for (MovementOrderDetailBo detailBo : bo.getDetails()) {
            MovementOrderDetail matched = null;
            if (detailBo.getId() != null) {
                matched = dbDetails.stream().filter(d -> Objects.equals(d.getId(), detailBo.getId())).findFirst().orElse(null);
            }
            if (matched == null) {
                matched = dbDetails.stream().filter(d -> Objects.equals(d.getSkuId(), detailBo.getSkuId())).findFirst().orElse(null);
            }
            if (matched == null) {
                continue;
            }
            BigDecimal add = detailBo.getQuantity() == null ? BigDecimal.ZERO : detailBo.getQuantity();
            if (putaway) {
                BigDecimal before = matched.getPutawayQuantity() == null ? BigDecimal.ZERO : matched.getPutawayQuantity();
                matched.setPutawayQuantity(before.add(add));
                if (StringUtils.isNotBlank(detailBo.getTargetStockCode())) {
                    // 入库完成后更新入库任务的货位编码
                    matched.setTargetStockCode(detailBo.getTargetStockCode());
                    matched.setStockCode(detailBo.getTargetStockCode());
                }
            } else {
                BigDecimal before = matched.getOperatedQuantity() == null ? BigDecimal.ZERO : matched.getOperatedQuantity();
                matched.setOperatedQuantity(before.add(add));
            }
            movementOrderDetailMapper.updateById(matched);
        }
        refreshStatus(bo.getId());
    }

    private void refreshStatus(Long orderId) {
        List<MovementOrderDetail> dbDetails = movementOrderDetailMapper.selectList(
            Wrappers.<MovementOrderDetail>lambdaQuery().eq(MovementOrderDetail::getOrderId, orderId));
        List<MovementOrderDetail> inRows = dbDetails.stream()
            .filter(d -> d.getDirection() != null && d.getDirection() == 2)
            .toList();
        List<MovementOrderDetail> checkRows = inRows.isEmpty() ? dbDetails : inRows;
        boolean allDone = !checkRows.isEmpty() && checkRows.stream().allMatch(d -> {
            BigDecimal putaway = d.getPutawayQuantity() == null ? BigDecimal.ZERO : d.getPutawayQuantity();
            BigDecimal plan = d.getQuantity() == null ? BigDecimal.ZERO : d.getQuantity();
            return putaway.compareTo(plan) >= 0;
        });
        movementOrderMapper.update(null, Wrappers.<MovementOrder>lambdaUpdate()
            .eq(MovementOrder::getId, orderId)
            .set(MovementOrder::getOrderStatus, allDone
                ? ServiceConstants.MovementOrderStatus.FINISH
                : ServiceConstants.MovementOrderStatus.PENDING));
    }
}
