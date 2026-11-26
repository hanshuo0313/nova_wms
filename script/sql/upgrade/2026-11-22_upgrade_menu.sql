SET NAMES utf8mb4;

-- 库存统计：库存查询 / 安全库存预警
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query_param`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
(2001,'库存查询',4,1,'query','wms/inventory/statistic',NULL,0,0,'C','1','1','wms:inventory:all','search','admin',NOW(),''),
(2002,'安全库存预警',4,2,'alert','wms/inventory/alert',NULL,0,0,'C','1','1','wms:inventory:all','warning','admin',NOW(),'');

UPDATE `sys_menu` SET `parent_id` = 4, `order_num` = 3 WHERE `menu_id` = 5;

-- 业务管理 目录 + 采购单 + 退货单
INSERT INTO `sys_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query_param`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
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

-- 新菜单授权给所有角色
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT r.`role_id`, m.`menu_id` FROM `sys_role` r
JOIN `sys_menu` m ON m.`menu_id` IN (2001,2002,2010,2011,2012,2013,2014,2015,2016,2017,2020,2021,2022,2023)
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` rm WHERE rm.`role_id` = r.`role_id` AND rm.`menu_id` = m.`menu_id`);
