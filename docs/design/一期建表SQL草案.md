# B2B 供应链订单履约平台一期建表 SQL 草案

## 1. 文档目标

本文档用于沉淀一期业务主链的建表草案，作为后续 Flyway 迁移脚本和实体设计的直接参考。

本文档关注两件事：

- 给出一期核心业务表的 SQL 建表草案
- 确保表和字段都带中文说明，便于后续维护和协作

当前内容为一期草案，不要求一次性覆盖所有后续能力，但要能支撑：

- 客户主数据
- 商品与价格
- 仓库与库存
- 订单与轨迹
- 出库与发货

同时，一期采用 `app_user.customer_id` 直接关联客户主体，不单独新增账号关系表。

## 2. 使用说明

- 当前 SQL 面向 `MySQL 8`
- 统一使用 `utf8mb4`
- 统一采用逻辑删除字段 `deleted`
- 统一保留 `created_by / created_time / updated_by / updated_time`
- 金额字段统一使用 `decimal(18,2)`
- 数量字段统一使用 `decimal(18,4)`
- 状态字段一期先使用 `varchar`，后续如需优化可再切枚举编码

## 3. 命名约定

- 业务表统一使用 `sc_` 前缀
- 主键统一使用 `id`
- 编号类字段统一使用 `*_code` 或 `*_no`
- 状态类字段统一使用 `*_status` 或 `status`
- 备注统一使用 `remark`

## 4. 建表顺序建议

建议按以下顺序落地：

1. 客户域
2. 商品域
3. 仓库库存域
4. 订单履约域

这样更符合实际开发顺序，也更容易分批验证。

## 5. 客户域

### 5.0 `app_user` 增量字段调整

一期建议在现有 `app_user` 基础上直接增加客户归属字段：

```sql
ALTER TABLE `app_user`
  ADD COLUMN `customer_id` bigint DEFAULT NULL COMMENT '客户ID' AFTER `display_name`,
  ADD KEY `idx_app_user_customer_id` (`customer_id`);
```

说明：

- `app_user` 负责登录身份
- `sc_customer` 负责客户主体
- 一期采用 `app_user.customer_id` 直接绑定客户主体，简化模型和查询路径

### 5.1 `sc_customer`

```sql
CREATE TABLE IF NOT EXISTS `sc_customer` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `customer_code` varchar(64) NOT NULL COMMENT '客户编码',
  `customer_name` varchar(128) NOT NULL COMMENT '客户名称',
  `customer_type` varchar(32) NOT NULL DEFAULT 'ENTERPRISE' COMMENT '客户类型',
  `customer_level` varchar(32) DEFAULT 'NORMAL' COMMENT '客户等级',
  `sales_owner_id` bigint DEFAULT NULL COMMENT '销售负责人ID',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '客户状态',
  `credit_enabled` tinyint NOT NULL DEFAULT 0 COMMENT '是否启用授信',
  `credit_limit` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '授信额度',
  `settlement_type` varchar(32) NOT NULL DEFAULT 'CASH' COMMENT '结算方式',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_customer_code` (`customer_code`),
  KEY `idx_sc_customer_name` (`customer_name`),
  KEY `idx_sc_customer_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户主表';
```

### 5.2 `sc_customer_contact`

```sql
CREATE TABLE IF NOT EXISTS `sc_customer_contact` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `contact_name` varchar(64) NOT NULL COMMENT '联系人姓名',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `phone` varchar(32) DEFAULT NULL COMMENT '座机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认联系人',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '联系人状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_customer_contact_customer_id` (`customer_id`),
  KEY `idx_sc_customer_contact_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户联系人表';
```

### 5.3 `sc_customer_address`

```sql
CREATE TABLE IF NOT EXISTS `sc_customer_address` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `province_code` varchar(32) DEFAULT NULL COMMENT '省编码',
  `city_code` varchar(32) DEFAULT NULL COMMENT '市编码',
  `district_code` varchar(32) DEFAULT NULL COMMENT '区编码',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '地址状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_customer_address_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户收货地址表';
```

## 6. 商品域

### 6.1 `sc_product_category`

```sql
CREATE TABLE IF NOT EXISTS `sc_product_category` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID',
  `category_name` varchar(128) NOT NULL COMMENT '分类名称',
  `category_code` varchar(64) NOT NULL COMMENT '分类编码',
  `level` int NOT NULL DEFAULT 1 COMMENT '分类层级',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '分类状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_product_category_code` (`category_code`),
  KEY `idx_sc_product_category_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

### 6.2 `sc_product_spu`

```sql
CREATE TABLE IF NOT EXISTS `sc_product_spu` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `spu_code` varchar(64) NOT NULL COMMENT 'SPU编码',
  `spu_name` varchar(128) NOT NULL COMMENT 'SPU名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `brand_name` varchar(64) DEFAULT NULL COMMENT '品牌名称',
  `unit_name` varchar(32) DEFAULT NULL COMMENT '基础单位名称',
  `sale_status` varchar(32) NOT NULL DEFAULT 'ON_SHELF' COMMENT '销售状态',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '数据状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_product_spu_code` (`spu_code`),
  KEY `idx_sc_product_spu_category_id` (`category_id`),
  KEY `idx_sc_product_spu_sale_status` (`sale_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';
```

### 6.3 `sc_product_sku`

```sql
CREATE TABLE IF NOT EXISTS `sc_product_sku` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `spu_id` bigint NOT NULL COMMENT 'SPU ID',
  `sku_name` varchar(128) NOT NULL COMMENT 'SKU名称',
  `spec_json` json DEFAULT NULL COMMENT '规格JSON',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '条码',
  `sale_unit` varchar(32) DEFAULT NULL COMMENT '销售单位',
  `package_size` decimal(18,4) DEFAULT 1.0000 COMMENT '包装规格数量',
  `min_order_qty` decimal(18,4) NOT NULL DEFAULT 1.0000 COMMENT '最小起订量',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'SKU状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_product_sku_code` (`sku_code`),
  KEY `idx_sc_product_sku_spu_id` (`spu_id`),
  KEY `idx_sc_product_sku_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';
```

### 6.4 `sc_price_policy`

```sql
CREATE TABLE IF NOT EXISTS `sc_price_policy` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `customer_id` bigint DEFAULT NULL COMMENT '客户ID，为空表示基础价',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `price_type` varchar(32) NOT NULL DEFAULT 'BASE' COMMENT '价格类型',
  `sale_price` decimal(18,2) NOT NULL COMMENT '销售单价',
  `min_order_qty` decimal(18,4) NOT NULL DEFAULT 1.0000 COMMENT '最小起订量',
  `effective_start_time` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end_time` datetime DEFAULT NULL COMMENT '生效结束时间',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '价格状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_price_policy_customer_sku` (`customer_id`, `sku_id`),
  KEY `idx_sc_price_policy_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='价格策略表';
```

## 7. 仓库库存域

### 7.1 `sc_warehouse`

```sql
CREATE TABLE IF NOT EXISTS `sc_warehouse` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `warehouse_code` varchar(64) NOT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(128) NOT NULL COMMENT '仓库名称',
  `warehouse_type` varchar(32) NOT NULL DEFAULT 'SELF' COMMENT '仓库类型',
  `region_code` varchar(32) DEFAULT NULL COMMENT '区域编码',
  `priority` int NOT NULL DEFAULT 0 COMMENT '履约优先级',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '仓库状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_warehouse_code` (`warehouse_code`),
  KEY `idx_sc_warehouse_region_code` (`region_code`),
  KEY `idx_sc_warehouse_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库主表';
```

### 7.2 `sc_inventory`

```sql
CREATE TABLE IF NOT EXISTS `sc_inventory` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `on_hand_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '在手库存',
  `available_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '可售库存',
  `locked_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '锁定库存',
  `safety_stock_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '安全库存',
  `status` varchar(32) NOT NULL DEFAULT 'ENABLED' COMMENT '库存状态',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_inventory_warehouse_sku` (`warehouse_id`, `sku_id`),
  KEY `idx_sc_inventory_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库库存表';
```

### 7.3 `sc_inventory_lock`

```sql
CREATE TABLE IF NOT EXISTS `sc_inventory_lock` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单明细ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `lock_qty` decimal(18,4) NOT NULL COMMENT '锁定数量',
  `lock_status` varchar(32) NOT NULL DEFAULT 'LOCKED' COMMENT '锁定状态',
  `lock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
  `unlock_time` datetime DEFAULT NULL COMMENT '释放时间',
  `deduct_time` datetime DEFAULT NULL COMMENT '扣减时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_inventory_lock_order_id` (`order_id`),
  KEY `idx_sc_inventory_lock_order_item_id` (`order_item_id`),
  KEY `idx_sc_inventory_lock_status` (`lock_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存锁定记录表';
```

### 7.4 `sc_inventory_log`

```sql
CREATE TABLE IF NOT EXISTS `sc_inventory_log` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `biz_type` varchar(32) NOT NULL COMMENT '业务类型',
  `biz_no` varchar(64) NOT NULL COMMENT '业务单号',
  `change_qty` decimal(18,4) NOT NULL COMMENT '变动数量',
  `before_available_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '变动前可售库存',
  `after_available_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '变动后可售库存',
  `before_locked_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '变动前锁定库存',
  `after_locked_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '变动后锁定库存',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_inventory_log_warehouse_sku` (`warehouse_id`, `sku_id`),
  KEY `idx_sc_inventory_log_biz_no` (`biz_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';
```

## 8. 订单履约域

### 8.1 `sc_order_main`

```sql
CREATE TABLE IF NOT EXISTS `sc_order_main` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `parent_order_id` bigint DEFAULT NULL COMMENT '父订单ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `customer_name` varchar(128) NOT NULL COMMENT '客户名称',
  `order_source` varchar(32) NOT NULL DEFAULT 'APP' COMMENT '订单来源',
  `order_status` varchar(32) NOT NULL COMMENT '订单主状态',
  `audit_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `fulfillment_status` varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '履约状态',
  `settlement_status` varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '结算状态',
  `after_sale_status` varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '售后状态',
  `total_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `discount_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `payable_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '应付金额',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) NOT NULL COMMENT '收货地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_order_main_order_no` (`order_no`),
  KEY `idx_sc_order_main_customer_status` (`customer_id`, `order_status`),
  KEY `idx_sc_order_main_submit_time` (`submit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';
```

### 8.2 `sc_order_item`

```sql
CREATE TABLE IF NOT EXISTS `sc_order_item` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(128) NOT NULL COMMENT 'SKU名称',
  `sale_unit` varchar(32) DEFAULT NULL COMMENT '销售单位',
  `sale_price` decimal(18,2) NOT NULL COMMENT '销售单价',
  `order_qty` decimal(18,4) NOT NULL COMMENT '下单数量',
  `locked_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '已锁定数量',
  `shipped_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '已发货数量',
  `received_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '已收货数量',
  `line_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '行金额',
  `warehouse_id` bigint DEFAULT NULL COMMENT '分配仓库ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_order_item_order_id` (`order_id`),
  KEY `idx_sc_order_item_sku_id` (`sku_id`),
  KEY `idx_sc_order_item_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

### 8.3 `sc_order_operation_log`

```sql
CREATE TABLE IF NOT EXISTS `sc_order_operation_log` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `operation_type` varchar(32) NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(255) NOT NULL COMMENT '操作描述',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人名称',
  `operator_type` varchar(32) DEFAULT NULL COMMENT '操作人类型',
  `before_status` varchar(64) DEFAULT NULL COMMENT '变更前状态',
  `after_status` varchar(64) DEFAULT NULL COMMENT '变更后状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_order_operation_log_order_id` (`order_id`),
  KEY `idx_sc_order_operation_log_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单业务轨迹表';
```

### 8.4 `sc_outbound_order`

```sql
CREATE TABLE IF NOT EXISTS `sc_outbound_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `outbound_no` varchar(64) NOT NULL COMMENT '出库单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `outbound_status` varchar(32) NOT NULL DEFAULT 'CREATED' COMMENT '出库状态',
  `pick_time` datetime DEFAULT NULL COMMENT '拣货时间',
  `review_time` datetime DEFAULT NULL COMMENT '复核时间',
  `outbound_time` datetime DEFAULT NULL COMMENT '出库时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_outbound_order_no` (`outbound_no`),
  KEY `idx_sc_outbound_order_order_id` (`order_id`),
  KEY `idx_sc_outbound_order_status` (`outbound_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';
```

### 8.5 `sc_outbound_item`

```sql
CREATE TABLE IF NOT EXISTS `sc_outbound_item` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `outbound_id` bigint NOT NULL COMMENT '出库单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单明细ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `plan_qty` decimal(18,4) NOT NULL COMMENT '计划出库数量',
  `actual_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '实际出库数量',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_outbound_item_outbound_id` (`outbound_id`),
  KEY `idx_sc_outbound_item_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';
```

### 8.6 `sc_shipment_order`

```sql
CREATE TABLE IF NOT EXISTS `sc_shipment_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `shipment_no` varchar(64) NOT NULL COMMENT '发货单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `outbound_id` bigint NOT NULL COMMENT '出库单ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `logistics_company` varchar(64) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `shipment_status` varchar(32) NOT NULL DEFAULT 'WAIT_SHIPMENT' COMMENT '发货状态',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `signed_time` datetime DEFAULT NULL COMMENT '签收时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_shipment_order_no` (`shipment_no`),
  KEY `idx_sc_shipment_order_order_id` (`order_id`),
  KEY `idx_sc_shipment_order_logistics_no` (`logistics_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货单表';
```

## 9. 外键策略建议

一期建议先以 `索引 + 应用层约束` 为主，不强制使用数据库外键。

原因：

- 微服务场景下更方便演进
- 迁移和初始化更灵活
- 避免影响批量导入和后续表结构调整

但在服务层必须保证以下关系正确：

- 客户、地址、联系人归属关系正确
- SPU、SKU、价格策略关系正确
- 仓库、库存、锁库关系正确
- 订单、明细、轨迹、出库、发货关系正确

## 10. 状态值建议

本文档中的状态字段建议先与设计文档保持一致：

- `order_status`
- `audit_status`
- `fulfillment_status`
- `outbound_status`
- `shipment_status`
- `lock_status`
- `status`

具体枚举值以以下文档为准：

- [订单状态机说明](./订单状态机说明.md)
- [库存与分仓设计说明](./库存与分仓设计说明.md)
- [出库发货设计说明](./出库发货设计说明.md)

## 11. 后续落地建议

本文档确认后，建议按以下顺序继续推进：

1. 基于本文档补正式 Flyway 脚本
2. 建实体、Mapper、枚举和领域服务接口
3. 先落客户、商品、仓库、库存基础表
4. 再落订单、出库、发货表
5. 最后补初始化演示数据
