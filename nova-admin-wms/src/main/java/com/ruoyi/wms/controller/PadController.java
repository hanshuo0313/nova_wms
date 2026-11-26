package com.ruoyi.wms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.constant.ServiceConstants;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.web.core.BaseController;
import com.ruoyi.wms.domain.bo.CheckOrderBo;
import com.ruoyi.wms.domain.bo.InventoryBo;
import com.ruoyi.wms.domain.bo.ReceiptOrderBo;
import com.ruoyi.wms.domain.bo.ShipmentOrderBo;
import com.ruoyi.wms.domain.bo.ShipmentOrderDetailBo;
import com.ruoyi.wms.domain.vo.CheckOrderVo;
import com.ruoyi.wms.domain.vo.InventoryVo;
import com.ruoyi.wms.domain.vo.ReceiptOrderVo;
import com.ruoyi.wms.domain.vo.ShipmentOrderVo;
import com.ruoyi.wms.service.CheckOrderService;
import com.ruoyi.wms.service.InventoryService;
import com.ruoyi.wms.service.MovementOrderService;
import com.ruoyi.wms.service.ReceiptOrderService;
import com.ruoyi.wms.service.ShipmentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * PAD 端接口（扫码出入库、移库、盘点、紧急出库）
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wms/pad")
public class PadController extends BaseController {

    private final InventoryService inventoryService;
    private final ReceiptOrderService receiptOrderService;
    private final ShipmentOrderService shipmentOrderService;
    private final CheckOrderService checkOrderService;
    private final MovementOrderService movementOrderService;

    /**
     * 待入库单列表
     */
    @SaCheckPermission("wms:receipt:all")
    @GetMapping("/receipt/orders")
    public TableDataInfo<ReceiptOrderVo> pendingReceipts(ReceiptOrderBo bo, PageQuery pageQuery) {
        bo.setOrderStatus(ServiceConstants.ReceiptOrderStatus.PENDING);
        return receiptOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 待出库单列表
     */
    @SaCheckPermission("wms:shipment:all")
    @GetMapping("/shipment/orders")
    public TableDataInfo<ShipmentOrderVo> pendingShipments(ShipmentOrderBo bo, PageQuery pageQuery) {
        bo.setOrderStatus(ServiceConstants.ShipmentOrderStatus.PENDING);
        return shipmentOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * 待盘点单列表
     */
    @SaCheckPermission("wms:check:all")
    @GetMapping("/check/orders")
    public TableDataInfo<CheckOrderVo> pendingChecks(CheckOrderBo bo, PageQuery pageQuery) {
        bo.setOrderStatus(ServiceConstants.CheckOrderStatus.PENDING);
        return checkOrderService.queryPageList(bo, pageQuery);
    }

    /**
     * PAD 库存查询：按仓库 + SKU 汇总（库存数为各货位合计）
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/inventory/summary")
    public TableDataInfo<InventoryVo> inventorySummary(InventoryBo bo, PageQuery pageQuery) {
        return inventoryService.querySummaryPage(bo, pageQuery);
    }

    /**
     * 扫码查询库存（货位编码 / 物料编码 / 条码）
     */
    @SaCheckPermission("wms:inventory:all")
    @GetMapping("/inventory/scan")
    public R<List<InventoryVo>> scanInventory(InventoryBo bo) {
        return R.ok(inventoryService.queryAll(bo));
    }

    /**
     * 紧急出库：扫码后直接创建并完成紧急领料出库单
     */
    @SaCheckPermission("wms:shipment:all")
    @PostMapping("/urgentShipment")
    public R<Long> urgentShipment(@RequestBody ShipmentOrderBo bo) {
        if (bo.getDetails() == null || bo.getDetails().isEmpty()) {
            return R.fail("请填写出库明细");
        }
        bo.setId(null);
        bo.setOrderNo("JJ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            + ThreadLocalRandom.current().nextInt(100, 999));
        bo.setOptType(2L);
        bo.setShipmentType(ServiceConstants.ShipmentType.URGENT);
        bo.setOrderStatus(ServiceConstants.ShipmentOrderStatus.FINISH);
        bo.setRemark("PAD 紧急领料出库");
        List<ShipmentOrderDetailBo> details = new ArrayList<>(bo.getDetails());
        details.forEach(d -> d.setId(null));
        bo.setDetails(details);
        shipmentOrderService.shipment(bo);
        return R.ok(bo.getId());
    }

    /**
     * 出库拣选缺货：该明细按计划数量标记完成（不扣库存）
     */
    @SaCheckPermission("wms:shipment:all")
    @PostMapping("/shipment/shortage")
    public R<Void> shipmentShortage(@RequestBody java.util.Map<String, Long> body) {
        Long orderId = body.get("orderId");
        Long detailId = body.get("detailId");
        if (orderId == null || detailId == null) {
            return R.fail("参数不完整");
        }
        shipmentOrderService.shortage(orderId, detailId);
        return R.ok();
    }

    /**
     * 移库：一次提交完成（源仓出库 + 目标仓入库）
     */
    @SaCheckPermission("wms:movement:all")
    @PostMapping("/movement/pick")
    public R<Void> movementPick(@RequestBody com.ruoyi.wms.domain.bo.MovementOrderBo bo) {
        movementOrderService.pick(bo);
        return R.ok();
    }

    /**
     * 移库上架：目标仓入库并累计上架数量
     */
    @SaCheckPermission("wms:movement:all")
    @PostMapping("/movement/putaway")
    public R<Void> movementPutaway(@RequestBody com.ruoyi.wms.domain.bo.MovementOrderBo bo) {
        movementOrderService.putaway(bo);
        return R.ok();
    }

    /**
     * 移库拣选缺货
     */
    @SaCheckPermission("wms:movement:all")
    @PostMapping("/movement/shortage")
    public R<Void> movementShortage(@RequestBody java.util.Map<String, Long> body) {
        Long orderId = body.get("orderId");
        Long detailId = body.get("detailId");
        if (orderId == null || detailId == null) {
            return R.fail("参数不完整");
        }
        movementOrderService.shortage(orderId, detailId);
        return R.ok();
    }

    @SaCheckPermission("wms:movement:all")
    @GetMapping("/movement/orders")
    public TableDataInfo<com.ruoyi.wms.domain.vo.MovementOrderVo> pendingMovements(
        com.ruoyi.wms.domain.bo.MovementOrderBo bo, PageQuery pageQuery) {
        bo.setOrderStatus(ServiceConstants.MovementOrderStatus.PENDING);
        return movementOrderService.queryPageList(bo, pageQuery);
    }
}
