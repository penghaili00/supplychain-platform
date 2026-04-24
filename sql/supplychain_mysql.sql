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
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级菜单ID，0表示顶级菜单',
  `menu_name` varchar(100) NOT NULL,
  `permission_code` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`)
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
(4011, '菜单权限更新', 'sys:menu:update', 1, 'system', 'system', 0),
(4101, '工作台', NULL, 1, 'system', 'system', 0),
(4102, '客户中心', NULL, 1, 'system', 'system', 0),
(4103, '商品中心', NULL, 1, 'system', 'system', 0),
(4104, '仓库库存', NULL, 1, 'system', 'system', 0),
(4105, '订单履约', NULL, 1, 'system', 'system', 0),
(4106, '系统管理', NULL, 1, 'system', 'system', 0),
(4111, '工作台首页', NULL, 1, 'system', 'system', 0),
(4112, '客户管理', NULL, 1, 'system', 'system', 0),
(4113, '客户联系人', NULL, 1, 'system', 'system', 0),
(4114, '收货地址', NULL, 1, 'system', 'system', 0),
(4115, '商品分类', NULL, 1, 'system', 'system', 0),
(4116, 'SPU 管理', NULL, 1, 'system', 'system', 0),
(4117, 'SKU 管理', NULL, 1, 'system', 'system', 0),
(4118, '价格策略', NULL, 1, 'system', 'system', 0),
(4119, '仓库管理', NULL, 1, 'system', 'system', 0),
(4120, '库存管理', NULL, 1, 'system', 'system', 0),
(4121, '库存流水', NULL, 1, 'system', 'system', 0),
(4122, '订单列表', NULL, 1, 'system', 'system', 0),
(4123, '订单审核', NULL, 1, 'system', 'system', 0),
(4124, '出库单', NULL, 1, 'system', 'system', 0),
(4125, '发货单', NULL, 1, 'system', 'system', 0),
(4126, '订单轨迹', NULL, 1, 'system', 'system', 0),
(4127, '后台用户', NULL, 1, 'system', 'system', 0),
(4128, '角色管理', NULL, 1, 'system', 'system', 0),
(4129, '菜单权限', NULL, 1, 'system', 'system', 0),
(4130, '操作日志', NULL, 1, 'system', 'system', 0),
(4201, '工作台首页查看', 'dashboard:home:view', 1, 'system', 'system', 0),
(4202, '客户管理查询', 'customer:customer:list', 1, 'system', 'system', 0),
(4203, '客户管理详情', 'customer:customer:view', 1, 'system', 'system', 0),
(4204, '客户管理创建', 'customer:customer:create', 1, 'system', 'system', 0),
(4205, '客户管理更新', 'customer:customer:update', 1, 'system', 'system', 0),
(4206, '客户管理启用', 'customer:customer:enable', 1, 'system', 'system', 0),
(4207, '客户管理停用', 'customer:customer:disable', 1, 'system', 'system', 0),
(4208, '客户联系人查询', 'customer:contact:list', 1, 'system', 'system', 0),
(4209, '客户联系人创建', 'customer:contact:create', 1, 'system', 'system', 0),
(4210, '客户联系人更新', 'customer:contact:update', 1, 'system', 'system', 0),
(4211, '客户联系人删除', 'customer:contact:delete', 1, 'system', 'system', 0),
(4212, '收货地址查询', 'customer:address:list', 1, 'system', 'system', 0),
(4213, '收货地址创建', 'customer:address:create', 1, 'system', 'system', 0),
(4214, '收货地址更新', 'customer:address:update', 1, 'system', 'system', 0),
(4215, '收货地址删除', 'customer:address:delete', 1, 'system', 'system', 0),
(4216, '商品分类查询', 'product:category:list', 1, 'system', 'system', 0),
(4217, '商品分类创建', 'product:category:create', 1, 'system', 'system', 0),
(4218, '商品分类更新', 'product:category:update', 1, 'system', 'system', 0),
(4219, '商品分类删除', 'product:category:delete', 1, 'system', 'system', 0),
(4220, 'SPU 管理查询', 'product:spu:list', 1, 'system', 'system', 0),
(4221, 'SPU 管理详情', 'product:spu:view', 1, 'system', 'system', 0),
(4222, 'SPU 管理创建', 'product:spu:create', 1, 'system', 'system', 0),
(4223, 'SPU 管理更新', 'product:spu:update', 1, 'system', 'system', 0),
(4224, 'SKU 管理查询', 'product:sku:list', 1, 'system', 'system', 0),
(4225, 'SKU 管理详情', 'product:sku:view', 1, 'system', 'system', 0),
(4226, 'SKU 管理创建', 'product:sku:create', 1, 'system', 'system', 0),
(4227, 'SKU 管理更新', 'product:sku:update', 1, 'system', 'system', 0),
(4228, 'SKU 管理启用', 'product:sku:enable', 1, 'system', 'system', 0),
(4229, 'SKU 管理停用', 'product:sku:disable', 1, 'system', 'system', 0),
(4230, '价格策略查询', 'pricing:policy:list', 1, 'system', 'system', 0),
(4231, '价格策略创建', 'pricing:policy:create', 1, 'system', 'system', 0),
(4232, '价格策略更新', 'pricing:policy:update', 1, 'system', 'system', 0),
(4233, '价格策略删除', 'pricing:policy:delete', 1, 'system', 'system', 0),
(4234, '仓库管理查询', 'warehouse:warehouse:list', 1, 'system', 'system', 0),
(4235, '仓库管理创建', 'warehouse:warehouse:create', 1, 'system', 'system', 0),
(4236, '仓库管理更新', 'warehouse:warehouse:update', 1, 'system', 'system', 0),
(4237, '仓库管理启用', 'warehouse:warehouse:enable', 1, 'system', 'system', 0),
(4238, '仓库管理停用', 'warehouse:warehouse:disable', 1, 'system', 'system', 0),
(4239, '库存管理查询', 'inventory:stock:list', 1, 'system', 'system', 0),
(4240, '库存管理详情', 'inventory:stock:view', 1, 'system', 'system', 0),
(4241, '库存管理调整', 'inventory:stock:adjust', 1, 'system', 'system', 0),
(4242, '库存流水查询', 'inventory:log:list', 1, 'system', 'system', 0),
(4243, '库存流水详情', 'inventory:log:view', 1, 'system', 'system', 0),
(4244, '订单列表查询', 'order:order:list', 1, 'system', 'system', 0),
(4245, '订单详情查看', 'order:order:view', 1, 'system', 'system', 0),
(4246, '订单取消', 'order:order:cancel', 1, 'system', 'system', 0),
(4247, '订单审核通过', 'order:order:audit', 1, 'system', 'system', 0),
(4248, '订单审核驳回', 'order:order:reject', 1, 'system', 'system', 0),
(4249, '出库单查询', 'fulfillment:outbound:list', 1, 'system', 'system', 0),
(4250, '出库单详情', 'fulfillment:outbound:view', 1, 'system', 'system', 0),
(4251, '出库单确认', 'fulfillment:outbound:confirm', 1, 'system', 'system', 0),
(4252, '发货单查询', 'shipment:shipment:list', 1, 'system', 'system', 0),
(4253, '发货单详情', 'shipment:shipment:view', 1, 'system', 'system', 0),
(4254, '发货单发货', 'shipment:shipment:deliver', 1, 'system', 'system', 0),
(4255, '订单轨迹查询', 'order:trace:list', 1, 'system', 'system', 0),
(4256, '订单轨迹详情', 'order:trace:view', 1, 'system', 'system', 0),
(4257, '操作日志详情', 'sys:log:view', 1, 'system', 'system', 0),
(4258, '客户 App 账号开通', 'customer:app-account:create', 1, 'system', 'system', 0),
(4259, '客户 App 账号更新', 'customer:app-account:update', 1, 'system', 'system', 0),
(4260, '客户 App 账号重置密码', 'customer:app-account:reset-password', 1, 'system', 'system', 0);

UPDATE `sys_menu`
SET `parent_id` = CASE `id`
  WHEN 4001 THEN 4127
  WHEN 4002 THEN 4130
  WHEN 4003 THEN 4111
  WHEN 4004 THEN 4127
  WHEN 4005 THEN 4127
  WHEN 4006 THEN 4128
  WHEN 4007 THEN 4128
  WHEN 4008 THEN 4128
  WHEN 4009 THEN 4129
  WHEN 4010 THEN 4129
  WHEN 4011 THEN 4129
  WHEN 4111 THEN 4101
  WHEN 4112 THEN 4102
  WHEN 4113 THEN 4102
  WHEN 4114 THEN 4102
  WHEN 4115 THEN 4103
  WHEN 4116 THEN 4103
  WHEN 4117 THEN 4103
  WHEN 4118 THEN 4103
  WHEN 4119 THEN 4104
  WHEN 4120 THEN 4104
  WHEN 4121 THEN 4104
  WHEN 4122 THEN 4105
  WHEN 4123 THEN 4105
  WHEN 4124 THEN 4105
  WHEN 4125 THEN 4105
  WHEN 4126 THEN 4105
  WHEN 4127 THEN 4106
  WHEN 4128 THEN 4106
  WHEN 4129 THEN 4106
  WHEN 4130 THEN 4106
  WHEN 4201 THEN 4111
  WHEN 4202 THEN 4112
  WHEN 4203 THEN 4112
  WHEN 4204 THEN 4112
  WHEN 4205 THEN 4112
  WHEN 4206 THEN 4112
  WHEN 4207 THEN 4112
  WHEN 4208 THEN 4113
  WHEN 4209 THEN 4113
  WHEN 4210 THEN 4113
  WHEN 4211 THEN 4113
  WHEN 4212 THEN 4114
  WHEN 4213 THEN 4114
  WHEN 4214 THEN 4114
  WHEN 4215 THEN 4114
  WHEN 4216 THEN 4115
  WHEN 4217 THEN 4115
  WHEN 4218 THEN 4115
  WHEN 4219 THEN 4115
  WHEN 4220 THEN 4116
  WHEN 4221 THEN 4116
  WHEN 4222 THEN 4116
  WHEN 4223 THEN 4116
  WHEN 4224 THEN 4117
  WHEN 4225 THEN 4117
  WHEN 4226 THEN 4117
  WHEN 4227 THEN 4117
  WHEN 4228 THEN 4117
  WHEN 4229 THEN 4117
  WHEN 4230 THEN 4118
  WHEN 4231 THEN 4118
  WHEN 4232 THEN 4118
  WHEN 4233 THEN 4118
  WHEN 4234 THEN 4119
  WHEN 4235 THEN 4119
  WHEN 4236 THEN 4119
  WHEN 4237 THEN 4119
  WHEN 4238 THEN 4119
  WHEN 4239 THEN 4120
  WHEN 4240 THEN 4120
  WHEN 4241 THEN 4120
  WHEN 4242 THEN 4121
  WHEN 4243 THEN 4121
  WHEN 4244 THEN 4122
  WHEN 4245 THEN 4122
  WHEN 4246 THEN 4122
  WHEN 4247 THEN 4123
  WHEN 4248 THEN 4123
  WHEN 4249 THEN 4124
  WHEN 4250 THEN 4124
  WHEN 4251 THEN 4124
  WHEN 4252 THEN 4125
  WHEN 4253 THEN 4125
  WHEN 4254 THEN 4125
  WHEN 4255 THEN 4126
  WHEN 4256 THEN 4126
  WHEN 4257 THEN 4130
  WHEN 4258 THEN 4112
  WHEN 4259 THEN 4112
  WHEN 4260 THEN 4112
  ELSE `parent_id`
END
WHERE `id` IN (
  4001, 4002, 4003, 4004, 4005, 4006, 4007, 4008, 4009, 4010, 4011,
  4111, 4112, 4113, 4114, 4115, 4116, 4117, 4118, 4119, 4120, 4121, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 4130,
  4201, 4202, 4203, 4204, 4205, 4206, 4207, 4208, 4209, 4210, 4211, 4212, 4213, 4214, 4215,
  4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 4224, 4225, 4226, 4227, 4228, 4229,
  4230, 4231, 4232, 4233, 4234, 4235, 4236, 4237, 4238, 4239, 4240, 4241, 4242, 4243,
  4244, 4245, 4246, 4247, 4248, 4249, 4250, 4251, 4252, 4253, 4254, 4255, 4256, 4257, 4258, 4259, 4260
);

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
(6011, 3001, 4011),
(6201, 3001, 4101),
(6202, 3001, 4102),
(6203, 3001, 4103),
(6204, 3001, 4104),
(6205, 3001, 4105),
(6206, 3001, 4106),
(6207, 3001, 4111),
(6208, 3001, 4112),
(6209, 3001, 4113),
(6210, 3001, 4114),
(6211, 3001, 4115),
(6212, 3001, 4116),
(6213, 3001, 4117),
(6214, 3001, 4118),
(6215, 3001, 4119),
(6216, 3001, 4120),
(6217, 3001, 4121),
(6218, 3001, 4122),
(6219, 3001, 4123),
(6220, 3001, 4124),
(6221, 3001, 4125),
(6222, 3001, 4126),
(6223, 3001, 4127),
(6224, 3001, 4128),
(6225, 3001, 4129),
(6226, 3001, 4130),
(6227, 3001, 4201),
(6228, 3001, 4202),
(6229, 3001, 4203),
(6230, 3001, 4204),
(6231, 3001, 4205),
(6232, 3001, 4206),
(6233, 3001, 4207),
(6234, 3001, 4208),
(6235, 3001, 4209),
(6236, 3001, 4210),
(6237, 3001, 4211),
(6238, 3001, 4212),
(6239, 3001, 4213),
(6240, 3001, 4214),
(6241, 3001, 4215),
(6242, 3001, 4216),
(6243, 3001, 4217),
(6244, 3001, 4218),
(6245, 3001, 4219),
(6246, 3001, 4220),
(6247, 3001, 4221),
(6248, 3001, 4222),
(6249, 3001, 4223),
(6250, 3001, 4224),
(6251, 3001, 4225),
(6252, 3001, 4226),
(6253, 3001, 4227),
(6254, 3001, 4228),
(6255, 3001, 4229),
(6256, 3001, 4230),
(6257, 3001, 4231),
(6258, 3001, 4232),
(6259, 3001, 4233),
(6260, 3001, 4234),
(6261, 3001, 4235),
(6262, 3001, 4236),
(6263, 3001, 4237),
(6264, 3001, 4238),
(6265, 3001, 4239),
(6266, 3001, 4240),
(6267, 3001, 4241),
(6268, 3001, 4242),
(6269, 3001, 4243),
(6270, 3001, 4244),
(6271, 3001, 4245),
(6272, 3001, 4246),
(6273, 3001, 4247),
(6274, 3001, 4248),
(6275, 3001, 4249),
(6276, 3001, 4250),
(6277, 3001, 4251),
(6278, 3001, 4252),
(6279, 3001, 4253),
(6280, 3001, 4254),
(6281, 3001, 4255),
(6282, 3001, 4256),
(6283, 3001, 4257),
(6284, 3001, 4258),
(6285, 3001, 4259),
(6286, 3001, 4260);

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
