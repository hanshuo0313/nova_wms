SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `wms_check_diff_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) NOT NULL COMMENT '差异单号',
  `check_order_id` bigint NOT NULL COMMENT '盘点单ID',
  `check_order_no` varchar(64) DEFAULT NULL COMMENT '盘点单号',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `status` int DEFAULT 0 COMMENT '状态 0待确认 1已确认',
  `total_diff` decimal(20,2) DEFAULT 0 COMMENT '差异合计',
  `remark` varchar(255) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_diff_check` (`check_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点差异单';

CREATE TABLE IF NOT EXISTS `wms_check_diff_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `diff_order_id` bigint NOT NULL COMMENT '差异单ID',
  `check_detail_id` bigint DEFAULT NULL COMMENT '盘点明细ID',
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `item_id` bigint DEFAULT NULL COMMENT '物料ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `inventory_id` bigint DEFAULT NULL COMMENT '库存行ID',
  `stock_code` varchar(255) DEFAULT NULL COMMENT '货位',
  `book_quantity` decimal(20,2) DEFAULT 0 COMMENT '账面数量',
  `check_quantity` decimal(20,2) DEFAULT 0 COMMENT '盘点数量',
  `diff_quantity` decimal(20,2) DEFAULT 0 COMMENT '差异数量',
  PRIMARY KEY (`id`),
  KEY `idx_diff_detail` (`diff_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点差异单明细';
