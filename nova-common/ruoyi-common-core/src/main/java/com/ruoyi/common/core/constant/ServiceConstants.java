package com.ruoyi.common.core.constant;

public class ServiceConstants {
    /**
     * 入库单状态
     */
    public class ReceiptOrderStatus {
        public static final Integer INVALID = -1;
        public static final Integer PENDING = 0;
        public static final Integer FINISH = 1;
    }

    /**
     * 出库单状态
     */
    public class ShipmentOrderStatus {
        public static final Integer INVALID = -1;
        public static final Integer PENDING = 0;
        public static final Integer FINISH = 1;
        /** 草稿：已保存未配货 */
        public static final Integer DRAFT = 2;
    }

    /**
     * 库存记录操作类型
     */
    public class InventoryHistoryOrderType {
        public static final Integer RECEIPT = 1;
        public static final Integer SHIPMENT = 2;
        public static final Integer MOVEMENT = 3;
        public static final Integer CHECK = 4;
    }

    /**
     * 移库单状态
     */
    public class MovementOrderStatus {
        public static final Integer INVALID = -1;
        public static final Integer PENDING = 0;
        public static final Integer FINISH = 1;
    }

    /**
     * 盘库单状态
     */
    public class CheckOrderStatus {
        public static final Integer INVALID = -1;
        public static final Integer PENDING = 0;
        public static final Integer FINISH = 1;
    }

    /**
     * 入库类型
     */
    public class ReceiptType {
        /** 采购入库 */
        public static final Integer PURCHASE = 1;
        /** 领料返库 */
        public static final Integer RETURN_MATERIAL = 2;
        /** 其他入库 */
        public static final Integer OTHER = 3;
    }

    /**
     * 出库类型
     */
    public class ShipmentType {
        /** 领料出库 */
        public static final Integer PICKING = 1;
        /** 紧急领料出库 */
        public static final Integer URGENT = 2;
        /** 退货出库 */
        public static final Integer RETURN = 3;
        /** 其他出库 */
        public static final Integer OTHER = 4;
    }

    /**
     * 采购单状态
     */
    public class PurchaseOrderStatus {
        public static final Integer DRAFT = 0;
        public static final Integer RUNNING = 1;
        public static final Integer FINISHED = 2;
        public static final Integer CLOSED = 3;
    }

    /**
     * 退货单状态
     */
    public class ReturnOrderStatus {
        public static final Integer INVALID = -1;
        public static final Integer PENDING = 0;
        public static final Integer FINISH = 1;
    }

    /**
     * 盘点方式
     */
    public class CheckType {
        /** 全盘 */
        public static final Integer ALL = 1;
        /** 动盘 */
        public static final Integer MOVING = 2;
        /** 指定 */
        public static final Integer SPECIFIED = 3;
    }
}
