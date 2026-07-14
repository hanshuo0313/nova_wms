SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
`table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
`table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
`table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
`sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
`sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
`class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
`tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
`package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
`module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
`business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
`function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
`function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
`gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
`gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
`options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
`column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
`table_id` bigint(20) NULL DEFAULT NULL COMMENT '归属表编号',
`column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
`column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
`column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
`java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
`java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
`is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
`is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
`is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
`is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
`is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
`is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
`is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
`query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
`html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
`dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
`sort` int(11) NULL DEFAULT NULL COMMENT '排序',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
`config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
`config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
`config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
`config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
`config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2024-06-13 16:06:37','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2024-06-13 16:06:37','',NULL,'初始化密码 123456');
INSERT INTO `sys_config` VALUES (3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2024-06-13 16:06:37','hs','2026-05-23 21:30:34','深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4,'账号自助-验证码开关','sys.account.captchaEnabled','true','Y','admin','2024-06-13 16:06:37','',NULL,'是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2024-06-13 16:06:37','admin','2026-05-23 02:09:24','是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6,'OSS预览列表资源开关','sys.oss.previewListResource','true','Y','admin','2024-06-13 16:06:37','admin','2026-06-28 18:06:17','true:开启, false:关闭');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
`dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
`parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
`ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
`dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
`order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
`leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
`phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
`email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
`del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100,0,'0','部门管理',0,'','','','1','0','admin','2024-06-13 16:06:25','admin','2026-05-27 18:22:59');
INSERT INTO `sys_dept` VALUES (101,100,'0,100','管理部门',1,'','','','1','0','admin','2024-06-13 16:06:25','admin','2026-05-23 01:55:43');
INSERT INTO `sys_dept` VALUES (102,100,'0,100','普通部门',2,NULL,NULL,NULL,'1','0','admin','2026-05-23 02:19:40','admin','2026-05-27 17:45:25');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
`dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
`dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
`dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
`dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
`dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
`css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
`list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
`is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0停用 1正常）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','1','admin','2024-06-13 16:06:36','',NULL,'性别男');
INSERT INTO `sys_dict_data` VALUES (2,2,'女','1','sys_user_sex','','','N','1','admin','2024-06-13 16:06:36','',NULL,'性别女');
INSERT INTO `sys_dict_data` VALUES (3,3,'未知','2','sys_user_sex','','','N','1','admin','2024-06-13 16:06:36','',NULL,'性别未知');
INSERT INTO `sys_dict_data` VALUES (4,1,'显示','1','sys_show_hide','','primary','Y','1','admin','2024-06-13 16:06:36','admin','2024-07-10 16:34:54','显示菜单');
INSERT INTO `sys_dict_data` VALUES (5,2,'隐藏','0','sys_show_hide','','danger','N','1','admin','2024-06-13 16:06:36','admin','2024-07-10 16:35:07','隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6,1,'正常','1','sys_normal_disable','','primary','Y','1','admin','2024-06-13 16:06:36','admin','2024-07-10 14:30:58','正常状态');
INSERT INTO `sys_dict_data` VALUES (7,2,'停用','0','sys_normal_disable','','danger','N','1','admin','2024-06-13 16:06:36','admin','2024-07-10 14:31:06','停用状态');
INSERT INTO `sys_dict_data` VALUES (8,1,'是','Y','sys_yes_no','','primary','Y','1','admin','2024-06-13 16:06:36','',NULL,'系统默认是');
INSERT INTO `sys_dict_data` VALUES (9,2,'否','N','sys_yes_no','','danger','N','1','admin','2024-06-13 16:06:36','',NULL,'系统默认否');
INSERT INTO `sys_dict_data` VALUES (10,1,'通知','1','sys_notice_type','','warning','Y','1','admin','2024-06-13 16:06:36','',NULL,'通知');
INSERT INTO `sys_dict_data` VALUES (11,2,'公告','2','sys_notice_type','','success','N','1','admin','2024-06-13 16:06:36','',NULL,'公告');
INSERT INTO `sys_dict_data` VALUES (12,1,'正常','1','sys_notice_status','','primary','Y','1','admin','2024-06-13 16:06:36','admin','2024-07-10 17:24:35','正常状态');
INSERT INTO `sys_dict_data` VALUES (13,2,'关闭','0','sys_notice_status','','danger','N','1','admin','2024-06-13 16:06:36','admin','2024-07-10 17:24:44','关闭状态');
INSERT INTO `sys_dict_data` VALUES (14,1,'新增','1','sys_oper_type','','info','N','1','admin','2024-06-13 16:06:36','',NULL,'新增操作');
INSERT INTO `sys_dict_data` VALUES (15,2,'修改','2','sys_oper_type','','info','N','1','admin','2024-06-13 16:06:36','',NULL,'修改操作');
INSERT INTO `sys_dict_data` VALUES (16,3,'删除','3','sys_oper_type','','danger','N','1','admin','2024-06-13 16:06:37','',NULL,'删除操作');
INSERT INTO `sys_dict_data` VALUES (17,4,'授权','4','sys_oper_type','','primary','N','1','admin','2024-06-13 16:06:37','',NULL,'授权操作');
INSERT INTO `sys_dict_data` VALUES (18,5,'导出','5','sys_oper_type','','warning','N','1','admin','2024-06-13 16:06:37','',NULL,'导出操作');
INSERT INTO `sys_dict_data` VALUES (19,6,'导入','6','sys_oper_type','','warning','N','1','admin','2024-06-13 16:06:37','',NULL,'导入操作');
INSERT INTO `sys_dict_data` VALUES (20,7,'强退','7','sys_oper_type','','danger','N','1','admin','2024-06-13 16:06:37','',NULL,'强退操作');
INSERT INTO `sys_dict_data` VALUES (21,8,'生成代码','8','sys_oper_type','','warning','N','1','admin','2024-06-13 16:06:37','',NULL,'生成操作');
INSERT INTO `sys_dict_data` VALUES (22,9,'清空数据','9','sys_oper_type','','danger','N','1','admin','2024-06-13 16:06:37','',NULL,'清空操作');
INSERT INTO `sys_dict_data` VALUES (23,1,'失败','0','sys_common_status','','danger','N','1','admin','2024-06-13 16:06:37','admin','2024-07-15 10:50:52','正常状态');
INSERT INTO `sys_dict_data` VALUES (24,2,'成功','1','sys_common_status','','success','N','1','admin','2024-06-13 16:06:37','admin','2024-07-15 10:51:05','停用状态');
INSERT INTO `sys_dict_data` VALUES (25,99,'其他','0','sys_oper_type','','info','N','1','admin','2024-06-13 16:06:36','',NULL,'其他操作');
INSERT INTO `sys_dict_data` VALUES (26,1,'空闲','1','wms_location_status',NULL,'primary','N','1','admin',NULL,'',NULL,NULL);
INSERT INTO `sys_dict_data` VALUES (27,1,'使用中','2','wms_location_status',NULL,'warning','N','1','admin',NULL,'',NULL,NULL);
INSERT INTO `sys_dict_data` VALUES (28,0,'客户','1','merchant_type',NULL,'default','N','1','admin','2024-07-15 11:35:46','admin','2024-07-16 11:21:11',NULL);
INSERT INTO `sys_dict_data` VALUES (29,1,'供应商','2','merchant_type',NULL,'default','N','1','admin','2024-07-15 11:45:03','admin','2024-07-16 11:21:29','');
INSERT INTO `sys_dict_data` VALUES (30,3,'客户/供应商','3','merchant_type',NULL,'default','N','1','admin','2024-07-16 11:21:48','admin','2024-07-16 11:21:48',NULL);
INSERT INTO `sys_dict_data` VALUES (31,0,'未入库','0','wms_receipt_status',NULL,'info','N','1','admin','2024-07-16 18:09:00','admin','2024-07-22 09:38:14',NULL);
INSERT INTO `sys_dict_data` VALUES (32,1,'已入库','1','wms_receipt_status',NULL,'primary','N','1','admin','2024-07-16 18:09:11','admin','2024-07-22 09:38:22',NULL);
INSERT INTO `sys_dict_data` VALUES (33,3,'作废','-1','wms_receipt_status',NULL,'danger','N','1','admin','2024-07-17 10:16:32','admin','2024-07-22 09:38:29',NULL);
INSERT INTO `sys_dict_data` VALUES (34,0,'生产入库','1','wms_receipt_type',NULL,'primary','N','1','admin','2024-07-19 16:42:12','admin','2024-07-22 09:38:50',NULL);
INSERT INTO `sys_dict_data` VALUES (35,1,'采购入库','2','wms_receipt_type',NULL,'primary','N','1','admin','2024-07-19 16:42:23','admin','2024-07-22 09:38:56',NULL);
INSERT INTO `sys_dict_data` VALUES (36,2,'退货入库','3','wms_receipt_type',NULL,'primary','N','1','admin','2024-07-19 16:42:35','admin','2024-07-22 09:39:01',NULL);
INSERT INTO `sys_dict_data` VALUES (37,3,'归还入库','4','wms_receipt_type',NULL,'primary','N','1','admin','2024-07-19 16:42:43','admin','2024-07-22 09:39:06',NULL);
INSERT INTO `sys_dict_data` VALUES (38,2,'作废','-1','wms_shipment_status',NULL,'danger','N','1','admin','2024-08-01 11:25:02','admin','2024-08-01 14:25:24',NULL);
INSERT INTO `sys_dict_data` VALUES (39,0,'未出库','0','wms_shipment_status',NULL,'info','N','1','admin','2024-08-01 11:25:29','admin','2024-08-01 14:25:37',NULL);
INSERT INTO `sys_dict_data` VALUES (40,1,'已出库','1','wms_shipment_status',NULL,'primary','N','1','admin','2024-08-01 11:25:42','admin','2024-08-01 14:25:32',NULL);
INSERT INTO `sys_dict_data` VALUES (41,0,'退货出库','1','wms_shipment_type',NULL,'primary','N','1','admin','2024-08-01 11:26:41','wms2_admin','2024-09-25 18:45:02',NULL);
INSERT INTO `sys_dict_data` VALUES (42,1,'销售出库','2','wms_shipment_type',NULL,'primary','N','1','admin','2024-08-01 11:26:51','wms2_admin','2024-09-25 18:45:13',NULL);
INSERT INTO `sys_dict_data` VALUES (43,2,'生产出库','3','wms_shipment_type',NULL,'primary','N','1','admin','2024-08-01 11:26:58','wms2_admin','2024-09-25 18:45:23',NULL);
INSERT INTO `sys_dict_data` VALUES (44,0,'入库','1','wms_inventory_history_type',NULL,'success','N','1','admin','2024-08-07 14:13:21','wms2_admin','2024-09-27 10:53:49',NULL);
INSERT INTO `sys_dict_data` VALUES (45,1,'出库','2','wms_inventory_history_type',NULL,'danger','N','1','admin','2024-08-07 14:13:36','wms2_admin','2024-09-27 10:53:39',NULL);
INSERT INTO `sys_dict_data` VALUES (46,2,'移库','3','wms_inventory_history_type',NULL,'warning','N','1','admin','2024-08-07 14:13:45','wms2_admin','2024-09-27 10:54:01',NULL);
INSERT INTO `sys_dict_data` VALUES (47,3,'盘库','4','wms_inventory_history_type',NULL,'primary','N','1','admin','2024-08-07 14:13:54','admin','2024-08-07 14:58:06',NULL);
INSERT INTO `sys_dict_data` VALUES (48,0,'未移库','0','wms_movement_status',NULL,'info','N','1','admin','2024-08-12 10:21:48','admin','2024-08-12 10:21:48',NULL);
INSERT INTO `sys_dict_data` VALUES (49,1,'已移库','1','wms_movement_status',NULL,'primary','N','1','admin','2024-08-12 10:21:59','admin','2024-08-12 10:21:59',NULL);
INSERT INTO `sys_dict_data` VALUES (50,2,'作废','-1','wms_movement_status',NULL,'danger','N','1','admin','2024-08-12 10:22:13','admin','2024-08-12 10:22:13',NULL);
INSERT INTO `sys_dict_data` VALUES (51,0,'待盘库','0','wms_check_status',NULL,'info','N','1','admin','2024-08-13 10:18:39','admin','2024-08-13 10:18:39',NULL);
INSERT INTO `sys_dict_data` VALUES (52,1,'已盘库','1','wms_check_status',NULL,'primary','N','1','admin','2024-08-13 10:18:52','admin','2024-08-13 10:18:52',NULL);
INSERT INTO `sys_dict_data` VALUES (53,2,'作废','-1','wms_check_status',NULL,'danger','N','1','admin','2024-08-13 10:19:09','admin','2024-08-13 10:19:09',NULL);
INSERT INTO `sys_dict_data` VALUES (54,1,'English','en','language_type',NULL,'default','N','1','admin','2026-06-18 00:20:51','admin','2026-06-18 00:20:51',NULL);
INSERT INTO `sys_dict_data` VALUES (55,2,'日本語','ja','language_type',NULL,'default','N','1','admin','2026-06-18 00:21:15','admin','2026-06-18 00:21:15',NULL);
INSERT INTO `sys_dict_data` VALUES (56,3,'한국어','ko','language_type',NULL,'default','N','1','admin','2026-06-18 00:21:32','admin','2026-06-18 00:21:32',NULL);
INSERT INTO `sys_dict_data` VALUES (57,1,'一般件','1','product_level',NULL,'default','N','1','admin','2026-06-18 22:55:47','admin','2026-06-18 22:55:47',NULL);
INSERT INTO `sys_dict_data` VALUES (58,2,'易损件','2','product_level',NULL,'default','N','1','admin','2026-06-18 22:56:07','admin','2026-06-18 22:56:07',NULL);
INSERT INTO `sys_dict_data` VALUES (59,3,'关键件','3','product_level',NULL,'default','N','1','admin','2026-06-18 22:56:25','admin','2026-06-18 22:56:25',NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
`dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
`dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
`dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`dict_id`) USING BTREE,
UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','1','admin','2024-06-13 16:06:35','',NULL,'用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2,'菜单状态','sys_show_hide','1','admin','2024-06-13 16:06:35','',NULL,'菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3,'系统开关','sys_normal_disable','1','admin','2024-06-13 16:06:35','',NULL,'系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4,'系统是否','sys_yes_no','1','admin','2024-06-13 16:06:35','',NULL,'系统是否列表');
INSERT INTO `sys_dict_type` VALUES (5,'通知类型','sys_notice_type','1','admin','2024-06-13 16:06:35','',NULL,'通知类型列表');
INSERT INTO `sys_dict_type` VALUES (6,'通知状态','sys_notice_status','1','admin','2024-06-13 16:06:35','',NULL,'通知状态列表');
INSERT INTO `sys_dict_type` VALUES (7,'操作类型','sys_oper_type','1','admin','2024-06-13 16:06:35','',NULL,'操作类型列表');
INSERT INTO `sys_dict_type` VALUES (8,'系统状态','sys_common_status','1','admin','2024-06-13 16:06:36','',NULL,'登录状态列表');
INSERT INTO `sys_dict_type` VALUES (9,'企业类型','merchant_type','1','admin','2024-07-15 11:35:34','admin','2024-07-16 17:41:32','企业类型');
INSERT INTO `sys_dict_type` VALUES (10,'入库状态','wms_receipt_status','1','admin','2024-07-16 18:02:04','admin','2024-07-16 18:02:17','入库状态');
INSERT INTO `sys_dict_type` VALUES (11,'入库类型','wms_receipt_type','1','admin','2024-07-19 16:41:51','admin','2024-07-19 16:41:51',NULL);
INSERT INTO `sys_dict_type` VALUES (12,'出库状态','wms_shipment_status','1','admin','2024-08-01 11:18:11','admin','2024-08-01 11:18:11',NULL);
INSERT INTO `sys_dict_type` VALUES (13,'出库类型','wms_shipment_type','1','admin','2024-08-01 11:18:26','admin','2024-08-01 11:18:26',NULL);
INSERT INTO `sys_dict_type` VALUES (14,'库存记录操作类型','wms_inventory_history_type','1','admin','2024-08-07 14:12:27','admin','2024-08-07 14:12:27',NULL);
INSERT INTO `sys_dict_type` VALUES (15,'移库状态','wms_movement_status','1','admin','2024-08-12 10:21:04','admin','2024-08-12 10:21:04',NULL);
INSERT INTO `sys_dict_type` VALUES (16,'盘库状态','wms_check_status','1','admin','2024-08-13 10:18:14','admin','2024-08-13 10:18:14',NULL);
INSERT INTO `sys_dict_type` VALUES (17,'库位状态','wms_location_status','1','admin','2026-05-29 01:49:00','admin','2026-05-29 01:49:00',NULL);
INSERT INTO `sys_dict_type` VALUES (18,'目标语言','language_type','1','admin','2026-06-18 00:19:09','admin','2026-06-18 00:19:09',NULL);
INSERT INTO `sys_dict_type` VALUES (19,'商品级别','product_level','1','admin','2026-06-18 22:54:58','admin','2026-06-18 22:54:58',NULL);

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
`info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
`user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
`ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
`login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
`browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
`os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
`msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
`login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
PRIMARY KEY (`info_id`) USING BTREE,
INDEX `idx_sys_logininfor_s`(`status`) USING BTREE,
INDEX `idx_sys_logininfor_lt`(`login_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
`menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
`menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
`parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
`order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
`path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
`component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
`query_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
`is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
`is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
`menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
`visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '显示状态（0隐藏 1显示）',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0停用 1正常）',
`perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
`icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1091 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,8,'system',NULL,'',0,0,'M','1','1','','system','admin','2024-06-13 16:06:26','admin','2026-06-21 02:47:42','系统管理目录');
INSERT INTO `sys_menu` VALUES (2,'系统监控',0,9,'monitor',NULL,'',0,0,'M','1','1','','monitor','admin','2024-06-13 16:06:26','admin','2026-06-21 02:47:47','系统监控目录');
INSERT INTO `sys_menu` VALUES (100,'用户管理',1,1,'user','system/user/index','',0,0,'C','1','1','system:user:list','user','admin','2024-06-13 16:06:26','',NULL,'用户管理菜单');
INSERT INTO `sys_menu` VALUES (101,'角色管理',1,2,'role','system/role/index','',0,0,'C','1','1','system:role:list','peoples','admin','2024-06-13 16:06:26','',NULL,'角色管理菜单');
INSERT INTO `sys_menu` VALUES (102,'菜单管理',1,3,'menu','system/menu/index','',0,0,'C','1','1','system:menu:list','tree-table','admin','2024-06-13 16:06:26','',NULL,'菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103,'部门管理',1,4,'dept','system/dept/index','',0,0,'C','0','0','system:dept:list','tree','admin','2024-06-13 16:06:26','admin','2026-06-21 20:24:45','部门管理菜单');
INSERT INTO `sys_menu` VALUES (104,'岗位管理',1,5,'post','system/post/index','',0,0,'C','0','0','system:post:list','post','admin','2024-06-13 16:06:26','admin','2026-06-08 01:08:24','岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105,'字典管理',1,6,'dict','system/dict/index','',0,0,'C','1','1','system:dict:list','dict','admin','2024-06-13 16:06:26','admin','2026-06-12 21:19:35','字典管理菜单');
INSERT INTO `sys_menu` VALUES (106,'参数设置',1,7,'config','system/config/index','',0,0,'C','1','1','system:config:list','edit','admin','2024-06-13 16:06:26','',NULL,'参数设置菜单');
INSERT INTO `sys_menu` VALUES (107,'通知公告',1,9,'notice','system/notice/index','',0,0,'C','1','1','system:notice:list','message','admin','2024-06-13 16:06:26','admin','2026-06-17 04:23:06','通知公告菜单');
INSERT INTO `sys_menu` VALUES (108,'日志管理',0,11,'log','','',0,0,'M','1','1','','log','admin','2024-06-13 16:06:27','admin','2026-06-21 02:48:17','日志管理菜单');
INSERT INTO `sys_menu` VALUES (109,'在线用户',2,1,'online','monitor/online/index','',0,0,'C','1','1','monitor:online:list','online','admin','2024-06-13 16:06:27','',NULL,'在线用户菜单');
INSERT INTO `sys_menu` VALUES (112,'缓存列表',2,6,'cacheList','monitor/cache/list','',0,0,'C','1','1','monitor:cache:list','redis-list','admin','2024-06-13 16:06:27','',NULL,'缓存列表菜单');
INSERT INTO `sys_menu` VALUES (113,'缓存监控',2,5,'cache','monitor/cache/index','',0,0,'C','1','1','monitor:cache:list','redis','admin','2024-06-13 16:06:27','',NULL,'缓存监控菜单');
INSERT INTO `sys_menu` VALUES (115,'代码生成',0,10,'gen','tool/gen/index','',0,0,'C','1','1','tool:gen:list','code','admin','2024-06-13 16:06:27','admin','2026-06-21 02:47:55','代码生成菜单');
INSERT INTO `sys_menu` VALUES (118,'文件管理',1,10,'oss','system/oss/index','',0,0,'C','1','1','system:oss:list','upload','admin','2024-06-13 16:06:27','',NULL,'文件管理菜单');
INSERT INTO `sys_menu` VALUES (500,'操作日志',108,1,'operlog','monitor/operlog/index','',0,0,'C','1','1','monitor:operlog:list','form','admin','2024-06-13 16:06:27','',NULL,'操作日志菜单');
INSERT INTO `sys_menu` VALUES (501,'登录日志',108,2,'logininfor','monitor/logininfor/index','',0,0,'C','1','1','monitor:logininfor:list','logininfor','admin','2024-06-13 16:06:27','',NULL,'登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001,'用户查询',100,1,'','','',0,0,'F','1','1','system:user:query','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1002,'用户新增',100,2,'','','',0,0,'F','1','1','system:user:add','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1003,'用户修改',100,3,'','','',0,0,'F','1','1','system:user:edit','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1004,'用户删除',100,4,'','','',0,0,'F','1','1','system:user:remove','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1005,'用户导出',100,5,'','','',0,0,'F','1','1','system:user:export','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1006,'用户导入',100,6,'','','',0,0,'F','1','1','system:user:import','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1007,'重置密码',100,7,'','','',0,0,'F','1','1','system:user:resetPwd','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1008,'角色查询',101,1,'','','',0,0,'F','1','1','system:role:query','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1009,'角色新增',101,2,'','','',0,0,'F','1','1','system:role:add','#','admin','2024-06-13 16:06:27','',NULL,'');
INSERT INTO `sys_menu` VALUES (1010,'角色修改',101,3,'','','',0,0,'F','1','1','system:role:edit','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1011,'角色删除',101,4,'','','',0,0,'F','1','1','system:role:remove','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1012,'角色导出',101,5,'','','',0,0,'F','1','1','system:role:export','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1013,'菜单查询',102,1,'','','',0,0,'F','1','1','system:menu:query','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1014,'菜单新增',102,2,'','','',0,0,'F','1','1','system:menu:add','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1015,'菜单修改',102,3,'','','',0,0,'F','1','1','system:menu:edit','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1016,'菜单删除',102,4,'','','',0,0,'F','1','1','system:menu:remove','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1017,'部门查询',103,1,'','','',0,0,'F','1','1','system:dept:query','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1018,'部门新增',103,2,'','','',0,0,'F','1','1','system:dept:add','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1019,'部门修改',103,3,'','','',0,0,'F','1','1','system:dept:edit','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1020,'部门删除',103,4,'','','',0,0,'F','1','1','system:dept:remove','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1021,'岗位查询',104,1,'','','',0,0,'F','1','1','system:post:query','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1022,'岗位新增',104,2,'','','',0,0,'F','1','1','system:post:add','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1023,'岗位修改',104,3,'','','',0,0,'F','1','1','system:post:edit','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1024,'岗位删除',104,4,'','','',0,0,'F','1','1','system:post:remove','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1025,'岗位导出',104,5,'','','',0,0,'F','1','1','system:post:export','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1026,'字典查询',105,1,'#','','',0,0,'F','1','1','system:dict:query','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1027,'字典新增',105,2,'#','','',0,0,'F','1','1','system:dict:add','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1028,'字典修改',105,3,'#','','',0,0,'F','1','1','system:dict:edit','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1029,'字典删除',105,4,'#','','',0,0,'F','1','1','system:dict:remove','#','admin','2024-06-13 16:06:28','',NULL,'');
INSERT INTO `sys_menu` VALUES (1030,'字典导出',105,5,'#','','',0,0,'F','1','1','system:dict:export','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1031,'参数查询',106,1,'#','','',0,0,'F','1','1','system:config:query','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1032,'参数新增',106,2,'#','','',0,0,'F','1','1','system:config:add','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1033,'参数修改',106,3,'#','','',0,0,'F','1','1','system:config:edit','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1034,'参数删除',106,4,'#','','',0,0,'F','1','1','system:config:remove','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1035,'参数导出',106,5,'#','','',0,0,'F','1','1','system:config:export','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1036,'公告查询',107,1,'#','','',0,0,'F','1','1','system:notice:query','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1037,'公告新增',107,2,'#','','',0,0,'F','1','1','system:notice:add','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1038,'公告修改',107,3,'#','','',0,0,'F','1','1','system:notice:edit','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1039,'公告删除',107,4,'#','','',0,0,'F','1','1','system:notice:remove','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1040,'操作查询',500,1,'#','','',0,0,'F','1','1','monitor:operlog:query','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1041,'操作删除',500,2,'#','','',0,0,'F','1','1','monitor:operlog:remove','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1042,'日志导出',500,4,'#','','',0,0,'F','1','1','monitor:operlog:export','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1043,'登录查询',501,1,'#','','',0,0,'F','1','1','monitor:logininfor:query','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1044,'登录删除',501,2,'#','','',0,0,'F','1','1','monitor:logininfor:remove','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1045,'日志导出',501,3,'#','','',0,0,'F','1','1','monitor:logininfor:export','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1046,'在线查询',109,1,'#','','',0,0,'F','1','1','monitor:online:query','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1047,'批量强退',109,2,'#','','',0,0,'F','1','1','monitor:online:batchLogout','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1048,'单条强退',109,3,'#','','',0,0,'F','1','1','monitor:online:forceLogout','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1050,'账户解锁',501,4,'#','','',0,0,'F','1','1','monitor:logininfor:unlock','#','admin','2024-06-13 16:06:29','',NULL,'');
INSERT INTO `sys_menu` VALUES (1055,'生成查询',115,1,'#','','',0,0,'F','1','1','tool:gen:query','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1056,'生成修改',115,2,'#','','',0,0,'F','1','1','tool:gen:edit','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1057,'生成删除',115,3,'#','','',0,0,'F','1','1','tool:gen:remove','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1058,'导入代码',115,2,'#','','',0,0,'F','1','1','tool:gen:import','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1059,'预览代码',115,4,'#','','',0,0,'F','1','1','tool:gen:preview','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1060,'生成代码',115,5,'#','','',0,0,'F','1','1','tool:gen:code','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1061,'文件查询',118,1,'#','','',0,0,'F','1','1','system:oss:query','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1062,'文件上传',118,2,'#','','',0,0,'F','1','1','system:oss:upload','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1063,'文件下载',118,3,'#','','',0,0,'F','1','1','system:oss:download','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1064,'文件删除',118,4,'#','','',0,0,'F','1','1','system:oss:remove','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1065,'配置添加',118,5,'#','','',0,0,'F','1','1','system:oss:add','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (1066,'配置编辑',118,6,'#','','',0,0,'F','1','1','system:oss:edit','#','admin','2024-06-13 16:06:30','',NULL,'');
INSERT INTO `sys_menu` VALUES (3,'基础资料',0,0,'basic',NULL,NULL,0,0,'M','1','1',NULL,'excel','admin','2024-07-04 15:01:48','admin','2026-06-20 18:31:23','');
INSERT INTO `sys_menu` VALUES (119,'往来单位',3,1,'merchant','wms/basic/merchant/index',NULL,0,0,'C','1','1','wms:merchant:list','documentation','admin','2024-07-05 11:58:12','admin','2024-08-27 16:41:53','往来单位菜单');
INSERT INTO `sys_menu` VALUES (1070,'单位查询',119,1,'#','',NULL,0,0,'F','1','1','wms:merchant:list','#','admin','2024-07-05 11:58:12','admin','2024-08-30 10:43:54','');
INSERT INTO `sys_menu` VALUES (1071,'单位新增',119,2,'#','',NULL,0,0,'F','1','1','wms:merchant:add','#','admin','2024-07-05 11:58:12','',NULL,'');
INSERT INTO `sys_menu` VALUES (1072,'单位修改',119,3,'#','',NULL,0,0,'F','1','1','wms:merchant:edit','#','admin','2024-07-05 11:58:12','',NULL,'');
INSERT INTO `sys_menu` VALUES (120,'仓库管理',3,2,'warehouse','wms/basic/warehouse/index',NULL,0,0,'C','1','1','wms:warehouse:list','documentation','admin','2024-07-17 14:17:51','wms2_admin','2024-09-10 13:38:53','');
INSERT INTO `sys_menu` VALUES (121,'商品管理',3,4,'item','wms/basic/item/index',NULL,0,0,'C','1','1','wms:item:list','documentation','admin','2024-07-18 14:16:33','admin','2024-08-27 16:43:06','');
INSERT INTO `sys_menu` VALUES (122,'品牌管理',3,3,'itemBrand','wms/basic/itemBrand/index',NULL,0,0,'C','1','1','wms:itemBrand:list','documentation','admin','2024-07-30 11:18:27','admin','2024-08-27 16:42:55','');
INSERT INTO `sys_menu` VALUES (1073,'仓库查询',120,1,'',NULL,NULL,0,0,'F','1','1','wms:warehouse:list','#','admin','2024-08-30 10:44:27','wms2_admin','2024-09-10 13:39:02','');
INSERT INTO `sys_menu` VALUES (1074,'仓库编辑',120,2,'',NULL,NULL,0,0,'F','1','1','wms:warehouse:edit','#','admin','2024-08-30 10:46:48','wms2_admin','2024-09-10 13:39:10','');
INSERT INTO `sys_menu` VALUES (1075,'仓库新增',120,2,'',NULL,NULL,0,0,'F','1','1','wms:itemBrand:add','#','admin','2024-08-30 10:46:48','wms2_admin','2024-09-10 13:39:10','');
INSERT INTO `sys_menu` VALUES (1076,'品牌查询',122,1,'',NULL,NULL,0,0,'F','1','1','wms:itemBrand:list','#','admin','2024-08-30 10:47:22','admin','2024-08-30 10:47:22','');
INSERT INTO `sys_menu` VALUES (1077,'品牌编辑',122,2,'',NULL,NULL,0,0,'F','1','1','wms:itemBrand:edit','#','admin','2024-08-30 10:50:27','admin','2024-08-30 10:50:27','');
INSERT INTO `sys_menu` VALUES (1078,'品牌新增',122,2,'',NULL,NULL,0,0,'F','1','1','wms:itemBrand:add','#','admin','2024-08-30 10:50:27','admin','2024-08-30 10:50:27','');
INSERT INTO `sys_menu` VALUES (1079,'商品查询',121,1,'',NULL,NULL,0,0,'F','1','1','wms:item:list','#','admin','2024-08-30 10:51:00','admin','2024-08-30 10:51:00','');
INSERT INTO `sys_menu` VALUES (1080,'商品编辑',121,2,'',NULL,NULL,0,0,'F','1','1','wms:item:edit','#','admin','2024-08-30 10:51:21','admin','2024-08-30 10:51:21','');
INSERT INTO `sys_menu` VALUES (4,'库存统计',0,1,'inventory','wms/inventory/statistic',NULL,0,0,'C','1','1','wms:inventory:all','chart','admin','2024-08-06 15:50:30','admin','2026-06-20 18:31:12','');
INSERT INTO `sys_menu` VALUES (5,'库存记录',0,3,'inventoryHistory','wms/inventory/history',NULL,0,0,'C','1','1','wms:inventoryHistory:all','list','admin','2024-08-07 14:46:13','admin','2024-08-30 08:58:13','');
INSERT INTO `sys_menu` VALUES (6,'入库',0,4,'receiptOrder','wms/order/receipt/index',NULL,0,0,'C','1','1','wms:receipt:all','exit-fullscreen','admin','2024-07-31 09:58:42','admin','2026-06-21 02:47:03','');
INSERT INTO `sys_menu` VALUES (123,'编辑入库单',0,1000,'receiptOrderEdit','wms/order/receipt/edit',NULL,0,0,'C','0','1','wms:receipt:edit','#','admin','2024-07-22 10:08:08','hs','2026-05-23 21:21:49','');
INSERT INTO `sys_menu` VALUES (7,'出库',0,5,'shipmentOrder','wms/order/shipment/index',NULL,0,0,'C','1','1','wms:shipment:all','fullscreen','admin','2024-08-01 11:43:04','admin','2026-06-21 02:47:12','');
INSERT INTO `sys_menu` VALUES (124,'编辑出库单',0,1000,'shipmentOrderEdit','wms/order/shipment/edit',NULL,0,0,'C','0','1','wms:shipment:edit','#','admin','2024-08-01 11:46:00','admin','2024-08-27 16:43:37','');
INSERT INTO `sys_menu` VALUES (8,'移库',0,6,'movementOrder','wms/order/movement/index',NULL,0,0,'C','1','1','wms:movement:all','drag','admin','2024-08-12 10:19:35','admin','2026-06-21 02:47:19','');
INSERT INTO `sys_menu` VALUES (125,'编辑移库单',0,1000,'movementOrderEdit','wms/order/movement/edit',NULL,0,0,'C','0','1','wms:movement:edit','#','admin','2024-08-12 13:07:00','admin','2024-08-27 16:43:50','');
INSERT INTO `sys_menu` VALUES (9,'盘库',0,7,'checkOrder','wms/order/check/index',NULL,0,0,'C','1','1','wms:check:all','example','admin','2024-08-13 10:38:08','admin','2026-06-21 02:47:27','');
INSERT INTO `sys_menu` VALUES (126,'编辑盘库单',0,1000,'checkOrderEdit','wms/order/check/edit',NULL,0,0,'C','0','1','wms:check:edit','#','admin','2024-08-13 10:51:36','admin','2024-08-27 16:43:44','');
INSERT INTO `sys_menu` VALUES (10,'库位管理',0,2,'location','wms/location/index','',0,0,'C','1','1','wms:location:list','tab','admin','2026-05-28 23:17:16','admin','2026-06-01 10:00:41','库位管理菜单');
INSERT INTO `sys_menu` VALUES (1081,'库位管理查询',10,1,'#','',NULL,0,0,'F','0','1','wms:location:query','#','admin','2026-05-28 23:17:16','admin','2026-05-28 23:34:30','');
INSERT INTO `sys_menu` VALUES (1082,'库位管理新增',10,2,'#','',NULL,0,0,'F','0','1','wms:location:add','#','admin','2026-05-28 23:17:16','admin','2026-05-28 23:34:34','');
INSERT INTO `sys_menu` VALUES (1083,'库位管理修改',10,3,'#','',NULL,0,0,'F','0','1','wms:location:edit','#','admin','2026-05-28 23:17:16','admin','2026-05-28 23:34:38','');
INSERT INTO `sys_menu` VALUES (1084,'库位管理删除',10,4,'#','',NULL,0,0,'F','0','1','wms:location:remove','#','admin','2026-05-28 23:17:16','admin','2026-05-28 23:34:41','');
INSERT INTO `sys_menu` VALUES (1085,'库位管理导出',10,5,'#','',NULL,0,0,'F','0','1','wms:location:export','#','admin','2026-05-28 23:17:16','admin','2026-05-28 23:34:44','');
INSERT INTO `sys_menu` VALUES (110,'术语词典',1,8,'glossary','wms/glossary/index',NULL,0,0,'C','1','1','wms:glossary:list','zip','admin','2026-06-17 03:44:06','admin','2026-06-17 04:24:05','术语词典菜单');
INSERT INTO `sys_menu` VALUES (1086,'术语词典查询',110,1,'#','',NULL,0,0,'F','0','1','wms:glossary:query','#','admin','2026-06-17 03:44:30','admin','2026-06-17 04:21:26','');
INSERT INTO `sys_menu` VALUES (1087,'术语词典新增',110,2,'#','',NULL,0,0,'F','0','1','wms:glossary:add','#','admin','2026-06-17 03:44:39','admin','2026-06-17 04:21:30','');
INSERT INTO `sys_menu` VALUES (1088,'术语词典修改',110,3,'#','',NULL,0,0,'F','0','1','wms:glossary:edit','#','admin','2026-06-17 03:44:44','admin','2026-06-17 04:21:34','');
INSERT INTO `sys_menu` VALUES (1089,'术语词典删除',110,4,'#','',NULL,0,0,'F','0','1','wms:glossary:remove','#','admin','2026-06-17 03:44:48','admin','2026-06-17 04:21:37','');
INSERT INTO `sys_menu` VALUES (1090,'术语词典导出',110,5,'#','',NULL,0,0,'F','0','1','wms:glossary:export','#','admin','2026-06-17 03:44:52','admin','2026-06-17 04:21:41','');


-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
`notice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
`notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
`notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
`notice_content` longblob COMMENT '公告内容',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='通知公告表';

-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
`oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
`title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
`business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
`method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
`request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
`operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
`oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
`dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
`oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
`oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
`oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
`oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
`json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
`status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0异常 1正常）',
`error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
`oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
PRIMARY KEY (`oper_id`) USING BTREE,
INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
`oss_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对象存储主键',
`file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
`original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原名',
`file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件后缀名',
`url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'URL地址',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '上传人',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新人',
`service` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'minio' COMMENT '服务商',
PRIMARY KEY (`oss_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OSS对象存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`  (
`oss_config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主建',
`config_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置key',
`access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'accessKey',
`secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '秘钥',
`bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '桶名称',
`prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '前缀',
`endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '访问站点',
`domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '自定义域名',
`is_https` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
`region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '域',
`access_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否默认（0=否,1=是）',
`ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '扩展字段',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`oss_config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '对象存储配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss_config
-- ----------------------------
INSERT INTO `sys_oss_config` VALUES (1,'minio','admin','admin123456','nova-wms','','127.0.0.1:9000','','N','','0','1','','admin','2024-06-13 16:06:38','admin','2026-06-29 20:26:43','');
INSERT INTO `sys_oss_config` VALUES (2,'qiniu','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','root','','s3-cn-north-1.qiniucs.com','','N','','0','0','','admin','2024-06-13 16:06:38','admin','2026-06-19 19:00:46',NULL);
INSERT INTO `sys_oss_config` VALUES (3,'aliyun','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','root','','oss-cn-beijing.aliyuncs.com','','N','','0','0','','admin','2024-06-13 16:06:38','admin','2026-06-19 19:00:49',NULL);
INSERT INTO `sys_oss_config` VALUES (4,'qcloud','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','root','','cos.ap-beijing.myqcloud.com','','N','ap-beijing','0','0','','admin','2024-06-13 16:06:38','admin','2024-06-13 16:06:38',NULL);
INSERT INTO `sys_oss_config` VALUES (5,'image','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','root','image','127.0.0.1:9000','','N','','0','0','','admin','2024-06-13 16:06:38','admin','2026-06-28 18:13:40',NULL);
INSERT INTO `sys_oss_config` VALUES (6,'rustfs','XXXXXXXXXXXXXXX','XXXXXXXXXXXXXXX','root','','127.0.0.1:9000','','N','','0','0','','admin','2024-06-13 16:06:38','admin','2026-06-28 18:13:40',NULL);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
 `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
 `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
 `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
 `post_sort` int(4) NOT NULL COMMENT '显示顺序',
 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
 `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
 PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
 `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
 `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
 `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
 `role_sort` int(4) NOT NULL COMMENT '显示顺序',
 `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
 `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
 `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
 `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
 `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
 `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
 `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
 PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'1','0','admin','2024-06-13 16:06:26','',NULL,'超级管理员');
INSERT INTO `sys_role` VALUES (2,'管理用户','manage',2,'1',1,1,'1','0','admin','2026-05-23 03:09:50','admin','2026-06-29 20:49:46',NULL);
INSERT INTO `sys_role` VALUES (3,'普通用户','common',3,'5',1,1,'1','0','admin','2026-05-23 03:12:14','admin','2026-06-29 20:48:19',NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
`role_id` bigint(20) NOT NULL COMMENT '角色ID',
`dept_id` bigint(20) NOT NULL COMMENT '部门ID',
PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
`role_id` bigint(20) NOT NULL COMMENT '角色ID',
`menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2,1);
INSERT INTO `sys_role_menu` VALUES (2,2);
INSERT INTO `sys_role_menu` VALUES (2,100);
INSERT INTO `sys_role_menu` VALUES (2,101);
INSERT INTO `sys_role_menu` VALUES (2,102);
INSERT INTO `sys_role_menu` VALUES (2,103);
INSERT INTO `sys_role_menu` VALUES (2,104);
INSERT INTO `sys_role_menu` VALUES (2,105);
INSERT INTO `sys_role_menu` VALUES (2,106);
INSERT INTO `sys_role_menu` VALUES (2,107);
INSERT INTO `sys_role_menu` VALUES (2,108);
INSERT INTO `sys_role_menu` VALUES (2,109);
INSERT INTO `sys_role_menu` VALUES (2,112);
INSERT INTO `sys_role_menu` VALUES (2,113);
INSERT INTO `sys_role_menu` VALUES (2,118);
INSERT INTO `sys_role_menu` VALUES (2,500);
INSERT INTO `sys_role_menu` VALUES (2,501);
INSERT INTO `sys_role_menu` VALUES (2,1001);
INSERT INTO `sys_role_menu` VALUES (2,1002);
INSERT INTO `sys_role_menu` VALUES (2,1003);
INSERT INTO `sys_role_menu` VALUES (2,1004);
INSERT INTO `sys_role_menu` VALUES (2,1005);
INSERT INTO `sys_role_menu` VALUES (2,1006);
INSERT INTO `sys_role_menu` VALUES (2,1007);
INSERT INTO `sys_role_menu` VALUES (2,1008);
INSERT INTO `sys_role_menu` VALUES (2,1009);
INSERT INTO `sys_role_menu` VALUES (2,1010);
INSERT INTO `sys_role_menu` VALUES (2,1011);
INSERT INTO `sys_role_menu` VALUES (2,1012);
INSERT INTO `sys_role_menu` VALUES (2,1013);
INSERT INTO `sys_role_menu` VALUES (2,1014);
INSERT INTO `sys_role_menu` VALUES (2,1015);
INSERT INTO `sys_role_menu` VALUES (2,1016);
INSERT INTO `sys_role_menu` VALUES (2,1017);
INSERT INTO `sys_role_menu` VALUES (2,1018);
INSERT INTO `sys_role_menu` VALUES (2,1019);
INSERT INTO `sys_role_menu` VALUES (2,1020);
INSERT INTO `sys_role_menu` VALUES (2,1021);
INSERT INTO `sys_role_menu` VALUES (2,1022);
INSERT INTO `sys_role_menu` VALUES (2,1023);
INSERT INTO `sys_role_menu` VALUES (2,1024);
INSERT INTO `sys_role_menu` VALUES (2,1025);
INSERT INTO `sys_role_menu` VALUES (2,1026);
INSERT INTO `sys_role_menu` VALUES (2,1027);
INSERT INTO `sys_role_menu` VALUES (2,1028);
INSERT INTO `sys_role_menu` VALUES (2,1029);
INSERT INTO `sys_role_menu` VALUES (2,1030);
INSERT INTO `sys_role_menu` VALUES (2,1031);
INSERT INTO `sys_role_menu` VALUES (2,1032);
INSERT INTO `sys_role_menu` VALUES (2,1033);
INSERT INTO `sys_role_menu` VALUES (2,1034);
INSERT INTO `sys_role_menu` VALUES (2,1035);
INSERT INTO `sys_role_menu` VALUES (2,1036);
INSERT INTO `sys_role_menu` VALUES (2,1037);
INSERT INTO `sys_role_menu` VALUES (2,1038);
INSERT INTO `sys_role_menu` VALUES (2,1039);
INSERT INTO `sys_role_menu` VALUES (2,1040);
INSERT INTO `sys_role_menu` VALUES (2,1041);
INSERT INTO `sys_role_menu` VALUES (2,1042);
INSERT INTO `sys_role_menu` VALUES (2,1043);
INSERT INTO `sys_role_menu` VALUES (2,1044);
INSERT INTO `sys_role_menu` VALUES (2,1045);
INSERT INTO `sys_role_menu` VALUES (2,1046);
INSERT INTO `sys_role_menu` VALUES (2,1047);
INSERT INTO `sys_role_menu` VALUES (2,1048);
INSERT INTO `sys_role_menu` VALUES (2,1050);
INSERT INTO `sys_role_menu` VALUES (2,1061);
INSERT INTO `sys_role_menu` VALUES (2,1062);
INSERT INTO `sys_role_menu` VALUES (2,1063);
INSERT INTO `sys_role_menu` VALUES (2,1064);
INSERT INTO `sys_role_menu` VALUES (2,1065);
INSERT INTO `sys_role_menu` VALUES (2,1066);
INSERT INTO `sys_role_menu` VALUES (2,3);
INSERT INTO `sys_role_menu` VALUES (2,119);
INSERT INTO `sys_role_menu` VALUES (2,1070);
INSERT INTO `sys_role_menu` VALUES (2,1071);
INSERT INTO `sys_role_menu` VALUES (2,1072);
INSERT INTO `sys_role_menu` VALUES (2,120);
INSERT INTO `sys_role_menu` VALUES (2,121);
INSERT INTO `sys_role_menu` VALUES (2,123);
INSERT INTO `sys_role_menu` VALUES (2,122);
INSERT INTO `sys_role_menu` VALUES (2,6);
INSERT INTO `sys_role_menu` VALUES (2,7);
INSERT INTO `sys_role_menu` VALUES (2,124);
INSERT INTO `sys_role_menu` VALUES (2,4);
INSERT INTO `sys_role_menu` VALUES (2,5);
INSERT INTO `sys_role_menu` VALUES (2,8);
INSERT INTO `sys_role_menu` VALUES (2,125);
INSERT INTO `sys_role_menu` VALUES (2,9);
INSERT INTO `sys_role_menu` VALUES (2,126);
INSERT INTO `sys_role_menu` VALUES (2,1073);
INSERT INTO `sys_role_menu` VALUES (2,1074);
INSERT INTO `sys_role_menu` VALUES (2,1077);
INSERT INTO `sys_role_menu` VALUES (2,1078);
INSERT INTO `sys_role_menu` VALUES (2,1075);
INSERT INTO `sys_role_menu` VALUES (2,1076);
INSERT INTO `sys_role_menu` VALUES (3,6);
INSERT INTO `sys_role_menu` VALUES (3,7);
INSERT INTO `sys_role_menu` VALUES (3,4);
INSERT INTO `sys_role_menu` VALUES (3,5);
INSERT INTO `sys_role_menu` VALUES (3,8);
INSERT INTO `sys_role_menu` VALUES (3,9);


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
`user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
`dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
`user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
`nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
`user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'sys_user' COMMENT '用户类型（sys_user系统用户）',
`email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
`phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
`sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
`avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
`password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
`del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
`login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
`login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1,101,'admin','超级管理员','sys_user','','','0','','$2a$10$9s14SAlohUiCtonoV9BgfeHQzQAYAkPPhzRvdNZlUrAUWxkacbZKG','1','0','0:0:0:0:0:0:0:1','2026-06-29 18:05:53','admin','2024-06-13 16:06:25','admin','2026-06-29 18:05:53','管理员');
INSERT INTO `sys_user` VALUES (2,101,'hs','管理员','sys_user','','','0','','$2a$10$0TovoLYrMisOcEwXyIImdu7QMARn4cYvOe/x63sd3hkIwjKFnjKMy','1','0','0:0:0:0:0:0:0:1','2026-06-21 17:44:12','admin','2026-05-23 03:17:56','hs','2026-06-21 17:44:12',NULL);
INSERT INTO `sys_user` VALUES (3,NULL,'gjy','普通用户','sys_user','','','0','','$2a$10$lE5bYDnDy3EKWX31Mp0r3uN003mE1OU.0kYfYwJfbZ41YwMdFwHJ.','1','0','',NULL,'admin','2026-06-20 23:17:02','admin','2026-06-20 23:17:02',NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
`user_id` bigint(20) NOT NULL COMMENT '用户ID',
`post_id` bigint(20) NOT NULL COMMENT '岗位ID',
PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
`user_id` bigint(20) NOT NULL COMMENT '用户ID',
`role_id` bigint(20) NOT NULL COMMENT '角色ID',
PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);

-- ----------------------------
-- Table structure for wms_check_order
-- ----------------------------
DROP TABLE IF EXISTS `wms_check_order`;
CREATE TABLE `wms_check_order`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_no` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盘点单号',
`order_status` tinyint(4) NULL DEFAULT 11 COMMENT '库存盘点单状态 -1：作废 0：未盘库 1：已盘库',
`total_quantity` decimal(20, 2) NULL DEFAULT NULL COMMENT '盈亏数',
`total_amount` decimal(10, 2) NULL DEFAULT NULL,
`warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '所属仓库',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存盘点单据' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_check_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_check_order_detail`;
CREATE TABLE `wms_check_order_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) NOT NULL COMMENT '盘点单id',
`sku_id` bigint(20) NOT NULL COMMENT '规格id',
`stock_code` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
`quantity` decimal(20,2) DEFAULT NULL COMMENT '库存数量',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`check_quantity` decimal(20,2) DEFAULT NULL COMMENT '盘点数量',
`warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
`inventory_id` bigint(20) DEFAULT NULL COMMENT '库存id',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存盘点单据详情';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_inventory
-- ----------------------------
DROP TABLE IF EXISTS `wms_inventory`;
CREATE TABLE `wms_inventory` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `sku_id` bigint(20) DEFAULT NULL COMMENT '规格ID',
 `warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
 `stock_code` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
 `quantity` decimal(20,2) DEFAULT NULL COMMENT '库存',
 `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
 `create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
 `update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存表';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_inventory_history
-- ----------------------------
DROP TABLE IF EXISTS `wms_inventory_history`;
CREATE TABLE `wms_inventory_history` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
`sku_id` bigint(20) DEFAULT NULL COMMENT '物料ID',
`stock_code` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
`quantity` decimal(20,2) DEFAULT NULL COMMENT '库存变化',
`before_quantity` decimal(20,2) DEFAULT NULL COMMENT '更新前数量',
`after_quantity` decimal(20,2) DEFAULT NULL COMMENT '更新后数量',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`order_id` bigint(20) DEFAULT NULL COMMENT '操作id（出库、入库、库存移动表单id）',
`order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作单号（入库、出库、移库、盘库单号）',
`order_type` int(11) DEFAULT NULL COMMENT '操作类型',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存记录';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_item
-- ----------------------------
DROP TABLE IF EXISTS `wms_item`;
CREATE TABLE `wms_item` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`item_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
`item_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
`item_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品图片(ossId,逗号分隔)',
`item_category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分类',
`unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单位类别',
`item_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品级别',
`item_brand` bigint(20) DEFAULT NULL COMMENT '品牌',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='物料';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_item_brand
-- ----------------------------
DROP TABLE IF EXISTS `wms_item_brand`;
CREATE TABLE `wms_item_brand`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`brand_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品牌名称',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品品牌表' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_item_category
-- ----------------------------
DROP TABLE IF EXISTS `wms_item_category`;
CREATE TABLE `wms_item_category`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物料类型id',
`parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父物料类型id',
`category_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '物料类型名称',
`order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
`status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '物料类型状态（0停用 1正常）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '物料类型表' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_item_sku
-- ----------------------------
DROP TABLE IF EXISTS `wms_item_sku`;
CREATE TABLE `wms_item_sku`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`sku_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规格名称',
`item_id` bigint(20) NULL DEFAULT NULL,
`barcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '条码',
`sku_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
`length` decimal(10, 1) NULL DEFAULT NULL COMMENT '长，单位cm',
`width` decimal(10, 1) NULL DEFAULT NULL COMMENT '宽，单位cm',
`height` decimal(10, 1) NULL DEFAULT NULL COMMENT '高，单位cm',
`gross_weight` decimal(10, 3) NULL DEFAULT NULL COMMENT '毛重，单位kg',
`net_weight` decimal(10, 3) NULL DEFAULT NULL COMMENT '净重，单位kg',
`cost_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价（元）',
`selling_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '销售价（元）',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sku信息' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_merchant
-- ----------------------------
DROP TABLE IF EXISTS `wms_merchant`;
CREATE TABLE `wms_merchant`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`merchant_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
`merchant_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
`merchant_type` tinyint(4) NULL DEFAULT NULL COMMENT '企业类型',
`merchant_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '级别',
`bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开户行',
`bank_account` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '银行账户',
`address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
`mobile` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
`tel` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '座机号',
`contact_person` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
`email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Email',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '往来单位' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_movement_order
-- ----------------------------
DROP TABLE IF EXISTS `wms_movement_order`;
CREATE TABLE `wms_movement_order`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
`source_warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '源仓库',
`target_warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '目标仓库',
`order_status` tinyint(4) NULL DEFAULT NULL COMMENT '状态',
`total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
`total_quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '总数量',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '移库单' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_movement_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_movement_order_detail`;
CREATE TABLE `wms_movement_order_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) DEFAULT NULL COMMENT '移库单Id',
`sku_id` bigint(20) DEFAULT NULL COMMENT '规格id',
`stock_code` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
`quantity` decimal(20,2) DEFAULT NULL COMMENT '数量',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`source_warehouse_id` bigint(20) DEFAULT NULL COMMENT '源仓库',
`target_warehouse_id` bigint(20) DEFAULT NULL COMMENT '目标仓库',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库存移动详情';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_receipt_order
-- ----------------------------
DROP TABLE IF EXISTS `wms_receipt_order`;
CREATE TABLE `wms_receipt_order`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入库单号',
`opt_type` int(11) NULL DEFAULT NULL COMMENT '入库类型',
`merchant_id` bigint(20) NULL DEFAULT NULL COMMENT '供应商',
`biz_order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务订单号',
`total_quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品总数',
`total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单金额',
`order_status` tinyint(4) NULL DEFAULT NULL COMMENT '入库状态',
`warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入库单' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_receipt_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_receipt_order_detail`;
CREATE TABLE `wms_receipt_order_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) DEFAULT NULL COMMENT '入库单号',
`sku_id` bigint(20) DEFAULT NULL COMMENT '规格id',
`quantity` decimal(20,2) DEFAULT NULL COMMENT '入库数量',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
`stock_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='入库单详情';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_shipment_order
-- ----------------------------
DROP TABLE IF EXISTS `wms_shipment_order`;
CREATE TABLE `wms_shipment_order`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_no` varchar(22) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '出库单号，系统自动生成',
`opt_type` int(11) NULL DEFAULT NULL COMMENT '出库类型',
`biz_order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务订单号',
`merchant_id` bigint(20) NULL DEFAULT NULL COMMENT '客户',
`total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
`total_quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '总数量',
`order_status` tinyint(4) NULL DEFAULT NULL COMMENT '出库单状态',
`warehouse_id` bigint(20) NULL DEFAULT NULL COMMENT '仓库id',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '出库单' ROW_FORMAT = Dynamic;

-- ----------------------------

-- ----------------------------
-- Table structure for wms_shipment_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `wms_shipment_order_detail`;
CREATE TABLE `wms_shipment_order_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) DEFAULT NULL COMMENT '出库单',
`warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
`sku_id` bigint(20) DEFAULT NULL COMMENT '规格id',
`quantity` decimal(10,2) DEFAULT NULL COMMENT '数量',
`stock_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '库位编号',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='出库单详情';

-- ----------------------------

-- ----------------------------
-- Table structure for wms_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `wms_warehouse`;
CREATE TABLE `wms_warehouse`  (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`warehouse_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
`warehouse_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
`order_num` bigint(20) NULL DEFAULT 0 COMMENT '排序',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) NULL DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) NULL DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '仓库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for inventory_location
-- ----------------------------
DROP TABLE IF EXISTS `inventory_location`;
CREATE TABLE `inventory_location` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`warehouse_id` bigint(20) NOT NULL COMMENT '所属仓库',
`stock_code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '库位编号',
`status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '库位状态：1：空闲、2：使用中',
`create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库位管理表';

-- ----------------------------
-- Table structure for wms_glossary
-- ----------------------------
DROP TABLE IF EXISTS `wms_glossary`;
CREATE TABLE `wms_glossary` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`source_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原文',
`target_text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '译文',
`target_lang` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'en' COMMENT '目标语言：en/ja/ko/de...',
`create_by` varchar(64) DEFAULT '' COMMENT '创建者',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
`update_by` varchar(64) DEFAULT '' COMMENT '更新者',
`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`),
UNIQUE KEY `uk_source_target` (`source_text`,`target_lang`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='术语词典';

-- ----------------------------
-- Records of wms_glossary
-- ----------------------------
INSERT INTO `wms_glossary` VALUES (1,'出库','outbound','en','','2026-06-17 03:07:03','admin','2026-06-17 05:17:21');
INSERT INTO `wms_glossary` VALUES (2,'出库','出荷伝票','ja','','2026-06-17 03:07:03','admin','2026-06-17 05:17:43');
INSERT INTO `wms_glossary` VALUES (3,'出库','출고전표','ko','','2026-06-17 03:07:03','admin','2026-06-17 05:17:52');
INSERT INTO `wms_glossary` VALUES (4,'入库','Inbound','en','','2026-06-17 03:07:03','admin','2026-06-17 05:18:20');
INSERT INTO `wms_glossary` VALUES (5,'入库','入荷伝票','ja','','2026-06-17 03:07:03','admin','2026-06-17 05:18:32');

-- ----------------------------

CREATE TABLE `wms_area` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '编号',
`area_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
`warehouse_id` bigint(20) NOT NULL COMMENT '所属仓库ID',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='库区';

-- ----------------------------

CREATE TABLE `wms_inventory_detail` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`receipt_order_id` bigint(20) DEFAULT NULL COMMENT '入库单id',
`receipt_order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '入库单类型',
`order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '单号',
`type` int(11) DEFAULT NULL COMMENT '类型 1：入库 2：移库 3：盘库',
`sku_id` bigint(20) DEFAULT NULL COMMENT 'sku的ID',
`warehouse_id` bigint(20) DEFAULT NULL COMMENT '所属仓库',
`area_id` bigint(20) DEFAULT NULL COMMENT '所属库区',
`quantity` decimal(20,2) DEFAULT NULL COMMENT '入库数量',
`batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_polish_ci DEFAULT NULL COMMENT '批号',
`production_date` datetime(3) DEFAULT NULL COMMENT '生产日期',
`expiration_date` datetime(3) DEFAULT NULL COMMENT '过期时间',
`amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
`remain_quantity` decimal(10,2) DEFAULT NULL COMMENT '剩余数量',
`create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_polish_ci DEFAULT NULL COMMENT '创建人',
`create_time` datetime(3) DEFAULT NULL COMMENT '创建时间',
`update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_polish_ci DEFAULT NULL COMMENT '修改人',
`update_time` datetime(3) DEFAULT NULL COMMENT '修改时间',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_polish_ci ROW_FORMAT=DYNAMIC COMMENT='库存详情';

-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;



