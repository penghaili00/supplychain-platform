package com.supplychain.service.provider.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.supplychain.common.core.enums.DataScopeType;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.service.api.rbac.role.command.RoleCreateCommand;
import com.supplychain.service.api.rbac.role.command.RoleUpdateCommand;
import com.supplychain.service.api.rbac.role.query.RoleQuery;
import com.supplychain.service.api.rbac.role.view.RoleView;
import com.supplychain.service.provider.rbac.entity.SysMenu;
import com.supplychain.service.provider.rbac.entity.SysRole;
import com.supplychain.service.provider.rbac.entity.SysRoleMenu;
import com.supplychain.service.provider.rbac.mapper.SysMenuMapper;
import com.supplychain.service.provider.rbac.mapper.SysRoleMapper;
import com.supplychain.service.provider.rbac.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public List<RoleView> listRoles(RoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = Wrappers.lambdaQuery(SysRole.class);
        wrapper.eq(SysRole::getDeleted, 0);
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            wrapper.and(q -> q.like(SysRole::getRoleKey, query.getKeyword())
                    .or()
                    .like(SysRole::getRoleName, query.getKeyword()));
        }
        if (query != null && query.getStatus() != null) {
            validateStatus(query.getStatus(), "角色状态");
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysRole::getId);
        return toViews(sysRoleMapper.selectList(wrapper));
    }

    public RoleView getRole(Long roleId) {
        return toViews(List.of(getRequiredRole(roleId))).stream()
                .findFirst()
                .orElseThrow(() -> new BizException(404, "角色不存在"));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateCommand command) {
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        String roleKey = normalizeRequiredText(command.getRoleKey(), "角色编码");
        assertUniqueRoleKey(roleKey, null);
        SysRole role = new SysRole();
        role.setRoleKey(roleKey);
        role.setRoleName(normalizeRequiredText(command.getRoleName(), "角色名称"));
        role.setDataScope(resolveDataScope(command.getDataScope()));
        role.setStatus(resolveStatus(command.getStatus()));
        if (sysRoleMapper.insert(role) != 1) {
            throw new BizException(500, "角色创建失败");
        }
        syncRoleMenus(role.getId(), command.getMenuIds());
        return role.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, RoleUpdateCommand command) {
        validatePositiveId(roleId, "角色ID");
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        SysRole role = getRequiredRole(roleId);
        String roleKey = normalizeRequiredText(command.getRoleKey(), "角色编码");
        assertUniqueRoleKey(roleKey, roleId);
        role.setRoleKey(roleKey);
        role.setRoleName(normalizeRequiredText(command.getRoleName(), "角色名称"));
        role.setDataScope(resolveDataScope(command.getDataScope()));
        role.setStatus(resolveStatus(command.getStatus()));
        if (sysRoleMapper.updateById(role) != 1) {
            throw new BizException(500, "角色更新失败");
        }
        syncRoleMenus(roleId, command.getMenuIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long roleId, Integer status) {
        validatePositiveId(roleId, "角色ID");
        validateStatus(status, "角色状态");
        SysRole role = getRequiredRole(roleId);
        role.setStatus(status);
        if (sysRoleMapper.updateById(role) != 1) {
            throw new BizException(500, "角色状态更新失败");
        }
    }

    private List<RoleView> toViews(List<SysRole> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return List.of();
        }
        List<Long> roleIds = roles.stream().map(SysRole::getId).toList();
        List<SysRoleMenu> relations = sysRoleMenuMapper.selectList(Wrappers.lambdaQuery(SysRoleMenu.class)
                .in(SysRoleMenu::getRoleId, roleIds));
        Map<Long, List<SysRoleMenu>> relationMap = relations.stream()
                .collect(Collectors.groupingBy(SysRoleMenu::getRoleId));
        List<Long> menuIds = relations.stream()
                .map(SysRoleMenu::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, SysMenu> menuMap = CollectionUtils.isEmpty(menuIds) ? Map.of()
                : sysMenuMapper.selectBatchIds(menuIds).stream()
                .filter(Objects::nonNull)
                .filter(menu -> menu.getDeleted() == null || menu.getDeleted() == 0)
                .collect(Collectors.toMap(SysMenu::getId, Function.identity()));
        return roles.stream().map(role -> {
            List<SysMenu> menus = relationMap.getOrDefault(role.getId(), List.of()).stream()
                    .map(item -> menuMap.get(item.getMenuId()))
                    .filter(Objects::nonNull)
                    .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
                    .toList();
            return RoleView.builder()
                    .roleId(role.getId())
                    .roleKey(role.getRoleKey())
                    .roleName(role.getRoleName())
                    .dataScope(role.getDataScope())
                    .status(role.getStatus())
                    .menuIds(menus.stream().map(SysMenu::getId).toList())
                    .menuNames(menus.stream().map(SysMenu::getMenuName).toList())
                    .permissionCodes(menus.stream()
                            .map(SysMenu::getPermissionCode)
                            .filter(StringUtils::hasText)
                            .toList())
                    .build();
        }).toList();
    }

    private SysRole getRequiredRole(Long roleId) {
        validatePositiveId(roleId, "角色ID");
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null || (role.getDeleted() != null && role.getDeleted() == 1)) {
            throw new BizException(404, "角色不存在");
        }
        return role;
    }

    private void syncRoleMenus(Long roleId, List<Long> menuIds) {
        validatePositiveId(roleId, "角色ID");
        List<Long> normalizedMenuIds = normalizeIds(menuIds, "菜单ID");
        ensureMenusExist(normalizedMenuIds);
        sysRoleMenuMapper.delete(Wrappers.lambdaQuery(SysRoleMenu.class)
                .eq(SysRoleMenu::getRoleId, roleId));
        for (Long menuId : normalizedMenuIds) {
            SysRoleMenu relation = new SysRoleMenu();
            relation.setRoleId(roleId);
            relation.setMenuId(menuId);
            if (sysRoleMenuMapper.insert(relation) != 1) {
                throw new BizException(500, "角色菜单关联保存失败");
            }
        }
    }

    private void assertUniqueRoleKey(String roleKey, Long excludeRoleId) {
        SysRole existing = sysRoleMapper.selectOne(Wrappers.lambdaQuery(SysRole.class)
                .eq(SysRole::getRoleKey, roleKey)
                .eq(SysRole::getDeleted, 0)
                .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), excludeRoleId)) {
            throw new BizException(400, "角色编码已存在");
        }
    }

    private void ensureMenusExist(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        List<SysMenu> menus = sysMenuMapper.selectBatchIds(menuIds).stream()
                .filter(Objects::nonNull)
                .filter(menu -> menu.getDeleted() == null || menu.getDeleted() == 0)
                .toList();
        if (menus.size() != menuIds.size()) {
            throw new BizException(400, "存在无效菜单");
        }
    }

    private List<Long> normalizeIds(List<Long> ids, String fieldName) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        LinkedHashSet<Long> values = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new BizException(400, fieldName + "不能为空");
            }
            values.add(id);
        }
        return new ArrayList<>(values);
    }

    private String resolveDataScope(String dataScope) {
        String value = normalizeRequiredText(dataScope, "数据权限范围");
        for (DataScopeType item : DataScopeType.values()) {
            if (item.name().equalsIgnoreCase(value)) {
                return item.name();
            }
        }
        throw new BizException(400, "数据权限范围不合法");
    }

    private Integer resolveStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        validateStatus(status, "角色状态");
        return status;
    }

    private void validateStatus(Integer status, String fieldName) {
        if (!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw new BizException(400, fieldName + "不合法");
        }
    }

    private void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BizException(400, fieldName + "不能为空");
        }
    }

    private String normalizeRequiredText(String text, String fieldName) {
        String value = normalizeOptionalText(text);
        if (!StringUtils.hasText(value)) {
            throw new BizException(400, fieldName + "不能为空");
        }
        return value;
    }

    private String normalizeOptionalText(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }
}
