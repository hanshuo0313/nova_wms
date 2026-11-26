package com.ruoyi.wms.service;

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
import com.ruoyi.wms.domain.bo.CheckOrderBo;
import com.ruoyi.wms.domain.bo.CheckOrderDetailBo;
import com.ruoyi.wms.domain.bo.InventoryBo;
import com.ruoyi.wms.domain.entity.CheckOrder;
import com.ruoyi.wms.domain.entity.CheckOrderDetail;
import com.ruoyi.wms.domain.entity.Inventory;
import com.ruoyi.wms.domain.vo.CheckOrderVo;
import com.ruoyi.wms.domain.vo.InventoryVo;
import com.ruoyi.wms.mapper.CheckOrderMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import com.ruoyi.wms.mapper.CheckOrderDetailMapper;
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
 * 库存盘点单据Service业务层处理
 *
 * @author zcc
 * @date 2024-08-13
 */
@RequiredArgsConstructor
@Service
public class CheckOrderService {

    private final CheckOrderMapper checkOrderMapper;
    private final CheckOrderDetailMapper checkOrderDetailMapper;
    private final CheckOrderDetailService checkOrderDetailService;
    private final InventoryService inventoryService;
    private final InventoryHistoryService inventoryHistoryService;
    private final CheckDiffService checkDiffService;

    /**
     * 查询库存盘点单据
     */
    public CheckOrderVo queryById(Long id){
        CheckOrderVo checkOrderVo = checkOrderMapper.selectVoById(id);
        if (checkOrderVo == null) {
            throw new BaseException("盘库单不存在");
        }
        checkOrderVo.setDetails(checkOrderDetailService.queryByCheckOrderId(id));
        return checkOrderVo;
    }

    /**
     * 查询库存盘点单据列表
     */
    public TableDataInfo<CheckOrderVo> queryPageList(CheckOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CheckOrder> lqw = buildQueryWrapper(bo);
        Page<CheckOrderVo> result = checkOrderMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询库存盘点单据列表
     */
    public List<CheckOrderVo> queryList(CheckOrderBo bo) {
        LambdaQueryWrapper<CheckOrder> lqw = buildQueryWrapper(bo);
        return checkOrderMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CheckOrder> buildQueryWrapper(CheckOrderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CheckOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), CheckOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOrderStatus() != null, CheckOrder::getOrderStatus, bo.getOrderStatus());
        lqw.eq(bo.getTotalQuantity() != null, CheckOrder::getTotalQuantity, bo.getTotalQuantity());
        lqw.eq(bo.getWarehouseId() != null, CheckOrder::getWarehouseId, bo.getWarehouseId());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(!warehouseScopeIds.isEmpty(), CheckOrder::getWarehouseId, warehouseScopeIds);
        lqw.eq(bo.getCheckType() != null, CheckOrder::getCheckType, bo.getCheckType());
        lqw.orderByDesc(BaseEntity::getCreateTime);
        return lqw;
    }

    /**
     * 新增库存盘点单据
     */
    @Transactional
    public void insertByBo(CheckOrderBo bo) {
        // 校验盘库单号唯一性
        validateCheckOrderNo(bo.getOrderNo());
        // 创建盘库单
        CheckOrder add = MapstructUtils.convert(bo, CheckOrder.class);
        if (add.getOrderStatus() == null) {
            add.setOrderStatus(ServiceConstants.CheckOrderStatus.PENDING);
        }
        checkOrderMapper.insert(add);
        // 创建盘库单明细
        List<CheckOrderDetail> addDetailList = MapstructUtils.convert(bo.getDetails(), CheckOrderDetail.class);
        addDetailList.forEach(it -> it.setOrderId(add.getId()));
        checkOrderDetailService.saveDetails(addDetailList);
        // 锁定盘点范围内的库存，避免盘点期间被出库
        List<CheckOrderDetail> savedDetails = checkOrderDetailMapper.selectList(
            Wrappers.<CheckOrderDetail>lambdaQuery().eq(CheckOrderDetail::getOrderId, add.getId()));
        for (CheckOrderDetail detail : savedDetails) {
            inventoryService.lock(detail.getInventoryId(), detail.getQuantity());
        }
    }

    /**
     * 取消盘点单：释放锁定库存
     */
    @Transactional
    public void cancel(Long id) {
        CheckOrder order = checkOrderMapper.selectById(id);
        if (order == null) {
            throw new BaseException("盘点单不存在");
        }
        if (ServiceConstants.CheckOrderStatus.FINISH.equals(order.getOrderStatus())) {
            throw new BaseException("盘点单已完成，不能取消");
        }
        List<CheckOrderDetail> details = checkOrderDetailMapper.selectList(
            Wrappers.<CheckOrderDetail>lambdaQuery().eq(CheckOrderDetail::getOrderId, id));
        for (CheckOrderDetail detail : details) {
            inventoryService.releaseLock(detail.getInventoryId(), detail.getQuantity());
        }
        checkOrderMapper.update(null, Wrappers.<CheckOrder>lambdaUpdate()
            .eq(CheckOrder::getId, id)
            .set(CheckOrder::getOrderStatus, ServiceConstants.CheckOrderStatus.INVALID));
    }

    private void validateCheckOrderNo(String checkOrderNo) {
        LambdaQueryWrapper<CheckOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(CheckOrder::getOrderNo, checkOrderNo);
        if (checkOrderMapper.exists(lambdaQueryWrapper)) {
            throw new BaseException("盘库单号重复，请手动修改");
        }
    }

    /**
     * 修改库存盘点单据
     */
    @Transactional
    public void updateByBo(CheckOrderBo bo) {
        // 更新盘库单
        CheckOrder update = MapstructUtils.convert(bo, CheckOrder.class);
        checkOrderMapper.updateById(update);
        // 保存盘库单明细
        List<CheckOrderDetail> detailList = MapstructUtils.convert(bo.getDetails(), CheckOrderDetail.class);
        detailList.forEach(it -> it.setOrderId(bo.getId()));
        checkOrderDetailService.saveDetails(detailList);
    }

    public void deleteById(Long id) {
        validateIdBeforeDelete(id);
        checkOrderMapper.deleteById(id);
    }

    private void validateIdBeforeDelete(Long id) {
        CheckOrderVo checkOrderVo = queryById(id);
        if (checkOrderVo == null) {
            throw new BaseException("盘库单不存在");
        }
        if (ServiceConstants.CheckOrderStatus.FINISH.equals(checkOrderVo.getOrderStatus())) {
            throw new ServiceException("盘库单【" + checkOrderVo.getOrderNo() + "】已盘库完成，无法删除！");
        }
    }

    /**
     * 批量删除库存盘点单据
     */
    public void deleteByIds(Collection<Long> ids) {
        checkOrderMapper.deleteBatchIds(ids);
    }

    /**
     * 按盘点方式生成盘点明细
     * 全盘：仓库全部库存；动盘：动碰次数不为0的库存；指定：按供应商/物料/货位筛选
     */
    public List<CheckOrderDetailBo> generateDetails(CheckOrderBo bo) {
        if (bo.getWarehouseId() == null) {
            throw new BaseException("请选择盘点仓库");
        }
        InventoryBo inventoryBo = new InventoryBo();
        inventoryBo.setWarehouseId(bo.getWarehouseId());
        if (ServiceConstants.CheckType.MOVING.equals(bo.getCheckType())) {
            inventoryBo.setMinMoveCount(1);
        } else if (ServiceConstants.CheckType.SPECIFIED.equals(bo.getCheckType())) {
            inventoryBo.setSupplierId(bo.getCheckSupplierId());
            inventoryBo.setItemId(bo.getCheckItemId());
            inventoryBo.setStockCode(bo.getCheckStockCode());
        }
        List<InventoryVo> inventoryList = inventoryService.queryAll(inventoryBo);
        List<CheckOrderDetailBo> details = new java.util.ArrayList<>();
        for (InventoryVo inventory : inventoryList) {
            CheckOrderDetailBo detail = new CheckOrderDetailBo();
            detail.setSkuId(inventory.getSkuId());
            detail.setQuantity(inventory.getQuantity());
            detail.setCheckQuantity(inventory.getQuantity());
            detail.setStockCode(inventory.getStockCode());
            detail.setWarehouseId(inventory.getWarehouseId());
            detail.setInventoryId(inventory.getId());
            details.add(detail);
        }
        return details;
    }

    /**
     * PAD 端逐项盘点确认：更新单项库存/盘点数，全部明细确认后关单
     */
    @Transactional
    public void padConfirm(Long orderId, Long detailId, BigDecimal checkQuantity) {
        CheckOrder order = checkOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BaseException("盘点单不存在");
        }
        CheckOrderDetail detail = checkOrderDetailMapper.selectById(detailId);
        if (detail == null) {
            throw new BaseException("盘点明细不存在");
        }
        if (checkQuantity == null || checkQuantity.signum() < 0) {
            throw new BaseException("盘点数量不能小于0");
        }
        // 当前账面库存
        Inventory inventory = null;
        if (detail.getInventoryId() != null) {
            inventory = inventoryService.getById(detail.getInventoryId());
        }
        if (inventory == null) {
            List<Inventory> list = inventoryService.list(Wrappers.<Inventory>lambdaQuery()
                .eq(Inventory::getWarehouseId, detail.getWarehouseId())
                .eq(Inventory::getSkuId, detail.getSkuId()));
            inventory = list.isEmpty() ? null : list.get(0);
        }
        BigDecimal book = inventory != null && inventory.getQuantity() != null ? inventory.getQuantity() : BigDecimal.ZERO;

        detail.setCheckQuantity(checkQuantity);
        detail.setChecked(1);
        checkOrderDetailMapper.updateById(detail);

        // 差异不直接改库存：有差异生成差异单，待确认差异单后再更新库存
        BigDecimal diff = checkQuantity.subtract(book);
        if (diff.signum() != 0) {
            checkDiffService.appendDiff(order, detail, book, checkQuantity, diff);
        } else if (detail.getInventoryId() != null) {
            inventoryService.releaseLock(detail.getInventoryId(), detail.getQuantity());
        }

        List<CheckOrderDetail> all = checkOrderDetailMapper.selectList(
            Wrappers.<CheckOrderDetail>lambdaQuery().eq(CheckOrderDetail::getOrderId, orderId));
        boolean allChecked = !all.isEmpty() && all.stream().allMatch(d -> d.getChecked() != null && d.getChecked() == 1);
        Integer status;
        if (!allChecked) {
            // 已开始盘点但未完成：盘库中
            status = 3;
        } else if (checkDiffService.hasPendingDiff(orderId)) {
            status = 2;
        } else {
            status = ServiceConstants.CheckOrderStatus.FINISH;
        }
        checkOrderMapper.update(null, Wrappers.<CheckOrder>lambdaUpdate()
            .eq(CheckOrder::getId, orderId)
            .set(CheckOrder::getOrderStatus, status));
    }

    /**
     * @param bo
     */
    @Transactional
    public void check(CheckOrderBo bo) {
        List<CheckOrderDetailBo> details = bo.getDetails();
        // 保存盘库单 check order
        if (Objects.isNull(bo.getId())) {
            insertByBo(bo);
        } else {
            updateByBo(bo);
        }
        // 保存库存 inventory
        inventoryService.updateInventory(details);
        // 盘点后动碰次数清零
        inventoryService.resetMoveCountBySku(bo.getWarehouseId(),
            details.stream().map(CheckOrderDetailBo::getSkuId).filter(Objects::nonNull).distinct().toList());
        // 新增库存记录 inventory history
        CheckOrderBo filterBo = this.filterCheckOrderDetail(bo);
        inventoryHistoryService.saveInventoryHistory(filterBo, ServiceConstants.InventoryHistoryOrderType.CHECK, true);
    }

    private CheckOrderBo filterCheckOrderDetail(CheckOrderBo bo) {
        CheckOrderBo filterBo = SerializationUtils.clone(bo);
        List<CheckOrderDetailBo> details = filterBo.getDetails().stream().filter(detail -> {
            BigDecimal result = detail.getCheckQuantity().subtract(detail.getQuantity());
            return result.signum() != 0;
        }).map(detail->{
            BigDecimal result = detail.getCheckQuantity().subtract(detail.getQuantity());
            detail.setBeforeQuantity(detail.getQuantity());
            detail.setAfterQuantity(detail.getCheckQuantity());
            detail.setQuantity(result);
            return detail;
        }).toList();
        filterBo.setDetails(details);
        return filterBo;
    }
}
