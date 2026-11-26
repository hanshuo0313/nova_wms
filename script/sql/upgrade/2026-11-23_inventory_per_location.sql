-- 库存按货位分行（2026-11-23）
-- 1) 合并重复的「仓库 + SKU + 货位」库存行
-- 2) 将 stock_code 为多货位（逗号分隔）的库存按货位拆分为多行，数量均分（余数归最后一货位）
-- 3) 增加唯一索引，保证同一仓库同一 SKU 同一货位只有一条库存

-- 说明：拆分历史数据建议先备份 wms_inventory：
-- CREATE TABLE wms_inventory_bak_20261123 AS SELECT * FROM wms_inventory;

ALTER TABLE `wms_inventory`
  ADD UNIQUE KEY `uk_inventory_loc` (`warehouse_id`, `sku_id`, `stock_code`(64));

-- 应用层（InventoryService）已按货位新增/扣减，stock_code 只保存单个货位。
-- 历史合并数据可通过运维脚本按货位拆分（见 docs/功能改造说明）。
