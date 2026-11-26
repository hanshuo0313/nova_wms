SET NAMES utf8mb4;

-- 入库/出库/移库/盘点 改为二级菜单：单 + 明细
UPDATE `sys_menu` SET `menu_name` = '入库', `menu_type` = 'M', `path` = 'receipt',
       `component` = NULL, `perms` = '', `order_num` = 4 WHERE `menu_id` = 6;
UPDATE `sys_menu` SET `menu_name` = '出库', `menu_type` = 'M', `path` = 'shipment',
       `component` = NULL, `perms` = '', `order_num` = 5 WHERE `menu_id` = 7;
UPDATE `sys_menu` SET `menu_name` = '移库', `menu_type` = 'M', `path` = 'movement',
       `component` = NULL, `perms` = '', `order_num` = 6 WHERE `menu_id` = 8;
UPDATE `sys_menu` SET `menu_name` = '盘点', `menu_type` = 'M', `path` = 'check',
       `component` = NULL, `perms` = '', `order_num` = 7 WHERE `menu_id` = 9;

INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query_param`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
(2101,'入库单',6,1,'order','wms/order/receipt/index',NULL,0,0,'C','1','1','wms:receipt:all','form','admin',NOW(),''),
(2102,'入库明细',6,2,'detail','wms/order/receipt/detail',NULL,0,0,'C','1','1','wms:receipt:all','list','admin',NOW(),''),
(2103,'出库单',7,1,'order','wms/order/shipment/index',NULL,0,0,'C','1','1','wms:shipment:all','form','admin',NOW(),''),
(2104,'出库明细',7,2,'detail','wms/order/shipment/detail',NULL,0,0,'C','1','1','wms:shipment:all','list','admin',NOW(),''),
(2105,'移库单',8,1,'order','wms/order/movement/index',NULL,0,0,'C','1','1','wms:movement:all','form','admin',NOW(),''),
(2106,'移库明细',8,2,'detail','wms/order/movement/detail',NULL,0,0,'C','1','1','wms:movement:all','list','admin',NOW(),''),
(2107,'盘点单',9,1,'order','wms/order/check/index',NULL,0,0,'C','1','1','wms:check:all','form','admin',NOW(),''),
(2108,'盘点明细',9,2,'detail','wms/order/check/detail',NULL,0,0,'C','1','1','wms:check:all','list','admin',NOW(),'');

INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT r.`role_id`, m.`menu_id` FROM `sys_role` r
JOIN `sys_menu` m ON m.`menu_id` IN (2101,2102,2103,2104,2105,2106,2107,2108)
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` rm WHERE rm.`role_id` = r.`role_id` AND rm.`menu_id` = m.`menu_id`);
