-- ============================================================
-- nova-wms 升级脚本 2026-11-22
-- 1) 往来单位 -> 供应商（沿用 wms_merchant 表）
-- 2) 商品扩展字段（供应商/型号/售价/交货期/安全库存）、SKU 体积
-- 3) 库存动碰次数
-- 4) 入库/出库类型与来源单
-- 5) 盘点方式与指定条件
-- 6) 采购单、退货单
-- 7) 菜单与字典调整
-- 执行方式：
--   mysql -u root -proot --default-character-set=utf8mb4 nova-wms < 2026-11-22_upgrade.sql
-- ============================================================
SET NAMES utf8mb4;

-- ---------- 1. 供应商 ----------
ALTER TABLE `wms_merchant` COMMENT = '供应商';
UPDATE `sys_menu` SET `menu_name` = '供应商'     WHERE `menu_id` = 119;
UPDATE `sys_menu` SET `menu_name` = '供应商查询' WHERE `menu_id` = 1070;
UPDATE `sys_menu` SET `menu_name` = '供应商新增' WHERE `menu_id` = 1071;
UPDATE `sys_menu` SET `menu_name` = '供应商修改' WHERE `menu_id` = 1072;

-- ---------- 2. 商品 ----------
ALTER TABLE `wms_item`
  ADD COLUMN `supplier_id`    bigint(20)    NULL COMMENT '供应商ID' AFTER `item_image`,
  ADD COLUMN `item_model`     varchar(100)  NULL COMMENT '型号' AFTER `item_brand`,
  ADD COLUMN `selling_price`  decimal(12,2) NULL DEFAULT 0 COMMENT '售价(元)' AFTER `item_model`,
  ADD COLUMN `delivery_days`  int(11)       NULL DEFAULT 0 COMMENT '交货期(天)' AFTER `selling_price`,
  ADD COLUMN `safety_stock`   decimal(18,2) NULL DEFAULT 0 COMMENT '安全库存水位' AFTER `delivery_days`,
  ADD INDEX `idx_item_supplier` (`supplier_id`);

ALTER TABLE `wms_item_sku`
  ADD COLUMN `volume` decimal(18,4) NULL DEFAULT 0 COMMENT '体积(cm³，长*宽*高)' AFTER `height`;

-- ---------- 3. 库存动碰次数 ----------
ALTER TABLE `wms_inventory`
  ADD COLUMN `move_count` int(11) NULL DEFAULT 0 COMMENT '动碰次数(出入库/移库+1，盘点后清零)';

-- ---------- 4. 入库/出库类型 ----------
ALTER TABLE `wms_receipt_order`
  ADD COLUMN `receipt_type`    int(11)     NULL DEFAULT 3 COMMENT '入库类型 1采购入库 2领料返库 3其他入库',
  ADD COLUMN `source_order_id` bigint(20)  NULL COMMENT '来源单ID(采购单/领料出库单)',
  ADD COLUMN `source_order_no` varchar(64) NULL COMMENT '来源单号';

ALTER TABLE `wms_shipment_order`
  ADD COLUMN `shipment_type`   int(11)     NULL DEFAULT 4 COMMENT '出库类型 1领料出库 2紧急领料出库 3退货出库 4其他出库',
  ADD COLUMN `source_order_id` bigint(20)  NULL COMMENT '来源单ID(领料出库单/退货单)',
  ADD COLUMN `source_order_no` varchar(64) NULL COMMENT '来源单号';

-- ---------- 5. 盘点方式 ----------
ALTER TABLE `wms_check_order`
  ADD COLUMN `check_type`        int(11)     NULL DEFAULT 1 COMMENT '盘点方式 1全盘 2动盘 3指定',
  ADD COLUMN `check_supplier_id` bigint(20)  NULL COMMENT '指定盘点-供应商',
  ADD COLUMN `check_item_id`     bigint(20)  NULL COMMENT '指定盘点-物料',
  ADD COLUMN `check_stock_code`  varchar(64) NULL COMMENT '指定盘点-货位编码';

-- ---------- 6. 采购单 ----------
CREATE TABLE `wms_purchase_order` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
  `order_no`    varchar(32)  DEFAULT NULL COMMENT '采购单号',
  `order_status` tinyint(4)  DEFAULT 0 COMMENT '0草稿 1执行中 2已完成 3已关闭',
  `warehouse_id` bigint(20)  DEFAULT NULL COMMENT '入库仓库',
  `total_quantity` decimal(20,2) DEFAULT 0 COMMENT '采购总数量',
  `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by`   varchar(64)  DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(3)  DEFAULT NULL COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(3)  DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_purchase_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单';

CREATE TABLE `wms_purchase_order_detail` (
  `id`                bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id`          bigint(20) NOT NULL COMMENT '采购单ID',
  `sku_id`            bigint(20) NOT NULL COMMENT '规格ID',
  `supplier_id`       bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `purchase_quantity` decimal(20,2) DEFAULT 0 COMMENT '采购数量',
  `inbound_quantity`  decimal(20,2) DEFAULT 0 COMMENT '入库数量',
  `return_quantity`   decimal(20,2) DEFAULT 0 COMMENT '退货数量',
  `actual_quantity`   decimal(20,2) DEFAULT 0 COMMENT '实收数量',
  `remark`            varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by`   varchar(64)  DEFAULT NULL,
  `create_time` datetime(3)  DEFAULT NULL,
  `update_by`   varchar(64)  DEFAULT NULL,
  `update_time` datetime(3)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_purchase_detail_order` (`order_id`),
  KEY `idx_purchase_detail_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细';

-- ---------- 7. 退货单 ----------
CREATE TABLE `wms_return_order` (
  `id`                bigint(20)  NOT NULL AUTO_INCREMENT,
  `order_no`          varchar(32) DEFAULT NULL COMMENT '退货单号',
  `purchase_order_id` bigint(20)  DEFAULT NULL COMMENT '关联采购单ID',
  `purchase_order_no` varchar(32) DEFAULT NULL COMMENT '关联采购单号',
  `order_status`      tinyint(4)  DEFAULT 0 COMMENT '0暂存 1已完成 -1作废',
  `warehouse_id`      bigint(20)  DEFAULT NULL COMMENT '退货仓库',
  `total_quantity`    decimal(20,2) DEFAULT 0 COMMENT '退货总数量',
  `remark`            varchar(500) DEFAULT NULL,
  `create_by`   varchar(64)  DEFAULT NULL,
  `create_time` datetime(3)  DEFAULT NULL,
  `update_by`   varchar(64)  DEFAULT NULL,
  `update_time` datetime(3)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_return_order_no` (`order_no`),
  KEY `idx_return_purchase_order` (`purchase_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货单';

CREATE TABLE `wms_return_order_detail` (
  `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id`           bigint(20) NOT NULL COMMENT '退货单ID',
  `purchase_detail_id` bigint(20) DEFAULT NULL COMMENT '采购单明细ID',
  `sku_id`             bigint(20) NOT NULL COMMENT '规格ID',
  `return_quantity`    decimal(20,2) DEFAULT 0 COMMENT '退货数量',
  `received_quantity`  decimal(20,2) DEFAULT 0 COMMENT '采购单实收数量(快照)',
  `max_quantity`       decimal(20,2) DEFAULT 0 COMMENT '可退数量(快照)',
  `remark`             varchar(255) DEFAULT NULL,
  `create_by`   varchar(64)  DEFAULT NULL,
  `create_time` datetime(3)  DEFAULT NULL,
  `update_by`   varchar(64)  DEFAULT NULL,
  `update_time` datetime(3)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_return_detail_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货单明细';

-- ---------- 8. 字典调整 ----------
DELETE FROM `sys_dict_data` WHERE `dict_type` IN ('wms_receipt_type','wms_shipment_type','wms_purchase_status','wms_return_status','wms_check_type');

INSERT INTO `sys_dict_data` (`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`is_default`,`create_by`,`create_time`,`remark`) VALUES
(1,'采购入库','1','wms_receipt_type','','primary','N','admin',NOW(),''),
(2,'领料返库','2','wms_receipt_type','','success','N','admin',NOW(),''),
(3,'其他入库','3','wms_receipt_type','','info','N','admin',NOW(),''),
(1,'领料出库','1','wms_shipment_type','','primary','N','admin',NOW(),''),
(2,'紧急领料出库','2','wms_shipment_type','','danger','N','admin',NOW(),''),
(3,'退货出库','3','wms_shipment_type','','warning','N','admin',NOW(),''),
(4,'其他出库','4','wms_shipment_type','','info','N','admin',NOW(),''),
(1,'草稿','0','wms_purchase_status','','info','N','admin',NOW(),''),
(2,'执行中','1','wms_purchase_status','','primary','N','admin',NOW(),''),
(3,'已完成','2','wms_purchase_status','','success','N','admin',NOW(),''),
(4,'已关闭','3','wms_purchase_status','','danger','N','admin',NOW(),''),
(1,'暂存','0','wms_return_status','','info','N','admin',NOW(),''),
(2,'已完成','1','wms_return_status','','success','N','admin',NOW(),''),
(3,'作废','-1','wms_return_status','','danger','N','admin',NOW(),''),
(1,'全盘','1','wms_check_type','','primary','N','admin',NOW(),''),
(2,'动盘','2','wms_check_type','','warning','N','admin',NOW(),''),
(3,'指定盘点','3','wms_check_type','','info','N','admin',NOW(),'');

DELETE FROM `sys_dict_type` WHERE `dict_type` IN ('wms_receipt_type','wms_shipment_type','wms_purchase_status','wms_return_status','wms_check_type');
INSERT INTO `sys_dict_type` (`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`remark`) VALUES
('入库类型','wms_receipt_type','0','admin',NOW(),'入库单类型'),
('出库类型','wms_shipment_type','0','admin',NOW(),'出库单类型'),
('采购单状态','wms_purchase_status','0','admin',NOW(),''),
('退货单状态','wms_return_status','0','admin',NOW(),''),
('盘点方式','wms_check_type','0','admin',NOW(),'');

-- ---------- 9. 菜单调整 ----------
-- 库位管理：从一级菜单移入基础资料(3)
UPDATE `sys_menu` SET `parent_id` = 3, `order_num` = 5 WHERE `menu_id` = 10;

-- 库存统计(4) 改为目录，新增 库存查询 / 安全库存预警，原库存记录(5) 移入其下
UPDATE `sys_menu` SET `menu_name` = '库存统计', `menu_type` = 'M', `path` = 'inventory',
       `component` = NULL, `perms` = '', `icon` = 'chart', `order_num` = 2, `visible` = '1'
WHERE `menu_id` = 4;

INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
(2001,'库存查询',4,1,'query','wms/inventory/statistic',NULL,0,0,'C','1','1','wms:inventory:all','search','admin',NOW(),''),
(2002,'安全库存预警',4,2,'alert','wms/inventory/alert',NULL,0,0,'C','1','1','wms:inventory:all','warning','admin',NOW(),'');

UPDATE `sys_menu` SET `parent_id` = 4, `order_num` = 3 WHERE `menu_id` = 5;

-- 业务管理 目录 + 采购单 + 退货单
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
(2010,'业务管理',0,1,'business',NULL,NULL,0,0,'M','1','1','','shopping','admin',NOW(),''),
(2011,'采购单',2010,1,'purchase','wms/business/purchase/index',NULL,0,0,'C','1','1','wms:purchase:list','form','admin',NOW(),''),
(2012,'退货单',2010,2,'return','wms/business/return/index',NULL,0,0,'C','1','1','wms:return:list','refund','admin',NOW(),''),
(2013,'采购单查询',2011,1,'','',NULL,0,0,'F','1','1','wms:purchase:list','#','admin',NOW(),''),
(2014,'采购单新增',2011,2,'','',NULL,0,0,'F','1','1','wms:purchase:add','#','admin',NOW(),''),
(2015,'采购单修改',2011,3,'','',NULL,0,0,'F','1','1','wms:purchase:edit','#','admin',NOW(),''),
(2016,'采购单删除',2011,4,'','',NULL,0,0,'F','1','1','wms:purchase:remove','#','admin',NOW(),''),
(2017,'采购单导出',2011,5,'','',NULL,0,0,'F','1','1','wms:purchase:export','#','admin',NOW(),''),
(2020,'退货单查询',2012,1,'','',NULL,0,0,'F','1','1','wms:return:list','#','admin',NOW(),''),
(2021,'退货单新增',2012,2,'','',NULL,0,0,'F','1','1','wms:return:add','#','admin',NOW(),''),
(2022,'退货单修改',2012,3,'','',NULL,0,0,'F','1','1','wms:return:edit','#','admin',NOW(),''),
(2023,'退货单删除',2012,4,'','',NULL,0,0,'F','1','1','wms:return:remove','#','admin',NOW(),'');

-- 新菜单授权给 admin(1) 及已有角色
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT r.`role_id`, m.`menu_id` FROM `sys_role` r
JOIN `sys_menu` m ON m.`menu_id` IN (2001,2002,2010,2011,2012,2013,2014,2015,2016,2017,2020,2021,2022,2023)
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` rm WHERE rm.`role_id` = r.`role_id` AND rm.`menu_id` = m.`menu_id`);
