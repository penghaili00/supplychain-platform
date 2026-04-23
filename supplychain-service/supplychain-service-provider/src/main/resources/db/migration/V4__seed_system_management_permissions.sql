INSERT INTO `sys_menu` (`id`, `menu_name`, `permission_code`, `status`, `created_by`, `updated_by`, `deleted`)
VALUES
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
  (4011, '菜单权限更新', 'sys:menu:update', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  `menu_name` = VALUES(`menu_name`),
  `permission_code` = VALUES(`permission_code`),
  `status` = VALUES(`status`),
  `updated_by` = VALUES(`updated_by`),
  `deleted` = VALUES(`deleted`);

INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`)
VALUES
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
  (6011, 3001, 4011)
ON DUPLICATE KEY UPDATE
  `role_id` = VALUES(`role_id`),
  `menu_id` = VALUES(`menu_id`);
