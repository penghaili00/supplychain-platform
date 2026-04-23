-- Compatibility bootstrap script.
-- Flyway migrations V1__init_schema.sql and V2__phase_one_b2b_schema.sql are the authoritative schema source.
CREATE DATABASE IF NOT EXISTS `supplychain` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `supplychain`;

DROP TABLE IF EXISTS `sc_shipment_order`;
DROP TABLE IF EXISTS `sc_outbound_item`;
DROP TABLE IF EXISTS `sc_outbound_order`;
DROP TABLE IF EXISTS `sc_order_operation_log`;
DROP TABLE IF EXISTS `sc_order_item`;
DROP TABLE IF EXISTS `sc_order_main`;
DROP TABLE IF EXISTS `sc_inventory_log`;
DROP TABLE IF EXISTS `sc_inventory_lock`;
DROP TABLE IF EXISTS `sc_inventory`;
DROP TABLE IF EXISTS `sc_warehouse`;
DROP TABLE IF EXISTS `sc_price_policy`;
DROP TABLE IF EXISTS `sc_product_sku`;
DROP TABLE IF EXISTS `sc_product_spu`;
DROP TABLE IF EXISTS `sc_product_category`;
DROP TABLE IF EXISTS `sc_customer_address`;
DROP TABLE IF EXISTS `sc_customer_contact`;
DROP TABLE IF EXISTS `sc_customer`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `app_user_role`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_operation_log`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `app_user`;
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `password` varchar(255) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `dept_id` bigint DEFAULT NULL,
  `dept_ancestors` varchar(255) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `app_user` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `password_salt` varchar(255) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_user_username` (`username`),
  KEY `idx_app_user_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role` (
  `id` bigint NOT NULL,
  `role_key` varchar(64) NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `data_scope` varchar(32) NOT NULL DEFAULT 'SELF',
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL,
  `menu_name` varchar(100) NOT NULL,
  `permission_code` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `app_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_app_user_role_user_id` (`user_id`),
  KEY `idx_app_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL,
  `operator_id` bigint DEFAULT NULL,
  `operator_name` varchar(100) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `business_type` varchar(32) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `request_uri` varchar(255) DEFAULT NULL,
  `request_method` varchar(20) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `request_param` longtext,
  `response_data` longtext,
  `success` tinyint NOT NULL DEFAULT 1,
  `message` varchar(500) DEFAULT NULL,
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sys_user` (`id`, `username`, `password`, `display_name`, `dept_id`, `dept_ancestors`, `status`, `created_by`, `updated_by`, `deleted`) VALUES
(1001, 'admin', '{noop}Admin@123', 'SupplyChain管理员', 100, ',100,', 1, 'system', 'system', 0);

INSERT INTO `app_user` (`id`, `username`, `password_hash`, `password_salt`, `display_name`, `customer_id`, `status`, `created_by`, `updated_by`, `deleted`) VALUES
(2001, 'demo', '/Aau8JFf63ZAAyaS7oaqEuP5IN/GbnCy48oC76mKNlY=', 'bXBIFnmlxK3G5KJMYFYmeA==', 'SupplyChain演示用户', 8001, 1, 'system', 'system', 0);

INSERT INTO `sys_role` (`id`, `role_key`, `role_name`, `data_scope`, `status`, `created_by`, `updated_by`, `deleted`) VALUES
(3001, 'super_admin', '超级管理员', 'ALL', 1, 'system', 'system', 0),
(3002, 'app_user', '用户端角色', 'SELF', 1, 'system', 'system', 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `permission_code`, `status`, `created_by`, `updated_by`, `deleted`) VALUES
(4001, '后台用户查询', 'sys:user:list', 1, 'system', 'system', 0),
(4002, '操作日志查询', 'sys:log:list', 1, 'system', 'system', 0),
(4003, '用户资料查看', 'app:profile:view', 1, 'system', 'system', 0),
(4004, '后台用户创建', 'sys:user:create', 1, 'system', 'system', 0),
(4005, '后台用户更新', 'sys:user:update', 1, 'system', 'system', 0),
(4006, '角色列表查询', 'sys:role:list', 1, 'system', 'system', 0),
(4007, '角色创建', 'sys:role:create', 1, 'system', 'system', 0),
(4008, '角色更新', 'sys:role:update', 1, 'system', 'system', 0),
(4009, '菜单权限查询', 'sys:menu:list', 1, 'system', 'system', 0),
(4010, '菜单权限创建', 'sys:menu:create', 1, 'system', 'system', 0),
(4011, '菜单权限更新', 'sys:menu:update', 1, 'system', 'system', 0);

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(5001, 1001, 3001);

INSERT INTO `app_user_role` (`id`, `user_id`, `role_id`) VALUES
(5002, 2001, 3002);

INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES
(6001, 3001, 4001),
(6002, 3001, 4002),
(6003, 3002, 4003),
(6004, 3001, 4004),
(6005, 3001, 4005),
(6006, 3001, 4006),
(6007, 3001, 4007),
(6008, 3001, 4008),
(6009, 3001, 4009),
(6010, 3001, 4010),
(6011, 3001, 4011);

CREATE TABLE `sc_customer` (
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
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_customer_code` (`customer_code`),
  KEY `idx_sc_customer_name` (`customer_name`),
  KEY `idx_sc_customer_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户主表';

CREATE TABLE `sc_customer_contact` (
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

CREATE TABLE `sc_customer_address` (
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

CREATE TABLE `sc_product_category` (
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

CREATE TABLE `sc_product_spu` (
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

CREATE TABLE `sc_product_sku` (
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

CREATE TABLE `sc_price_policy` (
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

CREATE TABLE `sc_warehouse` (
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

CREATE TABLE `sc_inventory` (
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

CREATE TABLE `sc_inventory_lock` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单明细ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `lock_qty` decimal(18,4) NOT NULL COMMENT '锁定数量',
  `lock_status` varchar(32) NOT NULL DEFAULT 'LOCKED' COMMENT '锁定状态',
  `lock_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间',
  `unlock_time` datetime(3) DEFAULT NULL COMMENT '释放时间',
  `deduct_time` datetime(3) DEFAULT NULL COMMENT '扣减时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_inventory_lock_order_id` (`order_id`),
  KEY `idx_sc_inventory_lock_order_item_id` (`order_item_id`),
  KEY `idx_sc_inventory_lock_status` (`lock_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存锁定记录表';

CREATE TABLE `sc_inventory_log` (
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
  `operate_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '操作时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_inventory_log_warehouse_sku` (`warehouse_id`, `sku_id`),
  KEY `idx_sc_inventory_log_biz_no` (`biz_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

CREATE TABLE `sc_order_main` (
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
  `submit_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '提交时间',
  `audit_time` datetime(3) DEFAULT NULL COMMENT '审核时间',
  `complete_time` datetime(3) DEFAULT NULL COMMENT '完成时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_order_main_order_no` (`order_no`),
  KEY `idx_sc_order_main_customer_status` (`customer_id`, `order_status`),
  KEY `idx_sc_order_main_submit_time` (`submit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

CREATE TABLE `sc_order_item` (
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
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_order_item_order_id` (`order_id`),
  KEY `idx_sc_order_item_sku_id` (`sku_id`),
  KEY `idx_sc_order_item_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

CREATE TABLE `sc_order_operation_log` (
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
  `operate_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '操作时间',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_order_operation_log_order_id` (`order_id`),
  KEY `idx_sc_order_operation_log_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单业务轨迹表';

CREATE TABLE `sc_outbound_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `outbound_no` varchar(64) NOT NULL COMMENT '出库单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `outbound_status` varchar(32) NOT NULL DEFAULT 'CREATED' COMMENT '出库状态',
  `pick_time` datetime(3) DEFAULT NULL COMMENT '拣货时间',
  `review_time` datetime(3) DEFAULT NULL COMMENT '复核时间',
  `outbound_time` datetime(3) DEFAULT NULL COMMENT '出库时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_outbound_order_no` (`outbound_no`),
  KEY `idx_sc_outbound_order_order_id` (`order_id`),
  KEY `idx_sc_outbound_order_status` (`outbound_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

CREATE TABLE `sc_outbound_item` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `outbound_id` bigint NOT NULL COMMENT '出库单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单明细ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `plan_qty` decimal(18,4) NOT NULL COMMENT '计划出库数量',
  `actual_qty` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '实际出库数量',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_sc_outbound_item_outbound_id` (`outbound_id`),
  KEY `idx_sc_outbound_item_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

CREATE TABLE `sc_shipment_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `shipment_no` varchar(64) NOT NULL COMMENT '发货单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `outbound_id` bigint NOT NULL COMMENT '出库单ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `logistics_company` varchar(64) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `shipment_status` varchar(32) NOT NULL DEFAULT 'WAIT_SHIPMENT' COMMENT '发货状态',
  `ship_time` datetime(3) DEFAULT NULL COMMENT '发货时间',
  `signed_time` datetime(3) DEFAULT NULL COMMENT '签收时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sc_shipment_order_no` (`shipment_no`),
  KEY `idx_sc_shipment_order_order_id` (`order_id`),
  KEY `idx_sc_shipment_order_logistics_no` (`logistics_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货单表';

INSERT INTO `sc_customer` (`id`, `customer_code`, `customer_name`, `customer_type`, `customer_level`, `sales_owner_id`, `status`, `credit_enabled`, `credit_limit`, `settlement_type`, `remark`, `created_by`, `updated_by`, `deleted`) VALUES
(8001, 'CUST_DEMO_001', 'SupplyChain演示采购客户', 'ENTERPRISE', 'NORMAL', 1001, 'ENABLED', 0, 0.00, 'CASH', '初始化演示客户', 'system', 'system', 0);
