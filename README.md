## 平台简介

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/zccbbg/ruoyi-fast-service/blob/master/LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-blue.svg)]()
[![JDK-17](https://img.shields.io/badge/JDK-17-green.svg)]()


是一套wms仓库管理系统，支持lodop和网页打印入库单、出库单。
* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。

## 前端项目地址
#### gitee
[https://gitee.com/zccbbg/ruo-yi-wms-vue](https://gitee.com/zccbbg/ruo-yi-wms-vue)

#### github
[https://github.com/zccbbg/RuoYi-WMS-VUE](https://github.com/zccbbg/RuoYi-WMS-VUE)


## 不同分支介绍
`lite` jdk17+vue3，支持多仓库，没有库区概念，操作简单，覆盖大部分库存应用场景。

`v1` jdk8+vue2

`master` 同步lite分支

## wms功能
1. 首页：库存预警与到期提醒、基础数据报表展示
2. 仓库/库区：管理维护仓库基础数据
3. 物料：管理维护物料基础数据
4. 客户/供应商：管理维护联系人基础数据
5. 入库：创建入库单后包括如下几个状态：暂存、作废、完成入库，入库类型包括：采购入库、外协入库、退货入库，入库单支持网页打印
6. 出库：创建出库单后包括如下几个状态：暂存、作废、完成出库，入库类型包括：销售出库、外协出库、调拨出库，出库单支持网页打印
7. 移库：创建移库单后包括如下几个状态：暂存、作废、完成移库
8. 盘库：创建盘库单后包括如下几个状态：暂存、作废、完成盘库 
9. 库存看板：查看当前物料库存数量，分仓库、库区、商品三个维度
10. 库存记录：查看当前物料库存操作记录
11. 库存明细：存放明细，分：仓库库区、商品两个维度


## 本地运行
https://docs.ichengle.top/wms/open/run2.html
## 系统部署文档
https://docs.ichengle.top/wms/open/ops2.html



