CREATE TABLE IF NOT EXISTS `sys_user` (
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

CREATE TABLE IF NOT EXISTS `app_user` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `password_salt` varchar(255) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `created_by` varchar(64) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(64) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_role` (
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

CREATE TABLE IF NOT EXISTS `sys_menu` (
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

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `app_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_app_user_role_user_id` (`user_id`),
  KEY `idx_app_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `sys_operation_log` (
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

INSERT INTO `sys_user` (`id`, `username`, `password`, `display_name`, `dept_id`, `dept_ancestors`, `status`, `created_by`, `updated_by`, `deleted`)
VALUES (1001, 'admin', '{noop}Admin@123', 'SupplyChain管理员', 100, ',100,', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  `password` = VALUES(`password`),
  `display_name` = VALUES(`display_name`),
  `dept_id` = VALUES(`dept_id`),
  `dept_ancestors` = VALUES(`dept_ancestors`),
  `status` = VALUES(`status`),
  `updated_by` = VALUES(`updated_by`),
  `deleted` = VALUES(`deleted`);

INSERT INTO `app_user` (`id`, `username`, `password_hash`, `password_salt`, `display_name`, `status`, `created_by`, `updated_by`, `deleted`)
VALUES (2001, 'demo', '/Aau8JFf63ZAAyaS7oaqEuP5IN/GbnCy48oC76mKNlY=', 'bXBIFnmlxK3G5KJMYFYmeA==', 'SupplyChain演示用户', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `password_salt` = VALUES(`password_salt`),
  `display_name` = VALUES(`display_name`),
  `status` = VALUES(`status`),
  `updated_by` = VALUES(`updated_by`),
  `deleted` = VALUES(`deleted`);

INSERT INTO `sys_role` (`id`, `role_key`, `role_name`, `data_scope`, `status`, `created_by`, `updated_by`, `deleted`)
VALUES
  (3001, 'super_admin', '超级管理员', 'ALL', 1, 'system', 'system', 0),
  (3002, 'app_user', '用户端角色', 'SELF', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`),
  `data_scope` = VALUES(`data_scope`),
  `status` = VALUES(`status`),
  `updated_by` = VALUES(`updated_by`),
  `deleted` = VALUES(`deleted`);

INSERT INTO `sys_menu` (`id`, `menu_name`, `permission_code`, `status`, `created_by`, `updated_by`, `deleted`)
VALUES
  (4001, '后台用户查询', 'sys:user:list', 1, 'system', 'system', 0),
  (4002, '操作日志查询', 'sys:log:list', 1, 'system', 'system', 0),
  (4003, '用户资料查看', 'app:profile:view', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  `menu_name` = VALUES(`menu_name`),
  `permission_code` = VALUES(`permission_code`),
  `status` = VALUES(`status`),
  `updated_by` = VALUES(`updated_by`),
  `deleted` = VALUES(`deleted`);

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`)
VALUES (5001, 1001, 3001)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `role_id` = VALUES(`role_id`);

INSERT INTO `app_user_role` (`id`, `user_id`, `role_id`)
VALUES (5002, 2001, 3002)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `role_id` = VALUES(`role_id`);

INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`)
VALUES
  (6001, 3001, 4001),
  (6002, 3001, 4002),
  (6003, 3002, 4003)
ON DUPLICATE KEY UPDATE
  `role_id` = VALUES(`role_id`),
  `menu_id` = VALUES(`menu_id`);
