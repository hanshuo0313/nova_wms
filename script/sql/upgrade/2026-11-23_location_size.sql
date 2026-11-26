SET NAMES utf8mb4;

-- 库位：货架号/列/层 + 尺寸
ALTER TABLE `inventory_location`
  ADD COLUMN `shelf_no` varchar(32) NULL COMMENT '货架号' AFTER `stock_code`,
  ADD COLUMN `col_no` varchar(16) NULL COMMENT '列' AFTER `shelf_no`,
  ADD COLUMN `layer_no` varchar(16) NULL COMMENT '层' AFTER `col_no`,
  ADD COLUMN `length` decimal(10,2) NULL COMMENT '长(cm)' AFTER `layer_no`,
  ADD COLUMN `width` decimal(10,2) NULL COMMENT '宽(cm)' AFTER `length`,
  ADD COLUMN `height` decimal(10,2) NULL COMMENT '高(cm)' AFTER `width`,
  ADD COLUMN `volume` decimal(14,2) NULL COMMENT '容积(cm³)' AFTER `height`;

-- 依据库位编码解析 货架-列-层（如 CS01-02-03）
UPDATE `inventory_location`
SET `shelf_no` = SUBSTRING_INDEX(`stock_code`, '-', 1),
    `col_no`   = SUBSTRING_INDEX(SUBSTRING_INDEX(`stock_code`, '-', 2), '-', -1),
    `layer_no` = SUBSTRING_INDEX(`stock_code`, '-', -1)
WHERE `stock_code` LIKE '%-%-%';

-- 初始状态：有库存使用的库位置为使用中(2)，其余空闲(1)
UPDATE `inventory_location` SET `status` = 1 WHERE `status` IS NULL;
UPDATE `inventory_location` loc
SET loc.`status` = 2
WHERE EXISTS (
  SELECT 1 FROM `wms_inventory` inv
  WHERE inv.`warehouse_id` = loc.`warehouse_id`
    AND inv.`quantity` > 0
    AND FIND_IN_SET(loc.`stock_code`, REPLACE(inv.`stock_code`, ' ', '')) > 0
);
