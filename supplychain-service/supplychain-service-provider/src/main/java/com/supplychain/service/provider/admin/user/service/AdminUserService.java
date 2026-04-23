package com.supplychain.service.provider.admin.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.mybatis.support.DataScopeSqlSupport;
import com.supplychain.service.api.admin.user.command.AdminUserCreateCommand;
import com.supplychain.service.api.admin.user.command.AdminUserUpdateCommand;
import com.supplychain.service.api.admin.user.query.AdminUserQuery;
import com.supplychain.service.api.admin.user.view.AdminUserView;
import com.supplychain.service.provider.admin.user.entity.SysUser;
import com.supplychain.service.provider.admin.user.mapper.SysUserMapper;
import com.supplychain.service.provider.rbac.entity.SysRole;
import com.supplychain.service.provider.rbac.entity.SysUserRole;
import com.supplychain.service.provider.rbac.mapper.SysRoleMapper;
import com.supplychain.service.provider.rbac.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AdminUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final DataScopeSqlSupport dataScopeSqlSupport;
    private final PasswordEncoder passwordEncoder;

    public List<AdminUserView> listUsers(SessionUser requester, AdminUserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class);
        wrapper.eq(SysUser::getDeleted, 0);
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            wrapper.and(q -> q.like(SysUser::getUsername, query.getKeyword())
                    .or()
                    .like(SysUser::getDisplayName, query.getKeyword()));
        }
        if (query != null && query.getStatus() != null) {
            validateStatus(query.getStatus(), "用户状态");
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        dataScopeSqlSupport.apply(wrapper, requester, SysUser::getDeptId, SysUser::getId, SysUser::getDeptAncestors);
        wrapper.orderByAsc(SysUser::getId);
        return toViews(sysUserMapper.selectList(wrapper));
    }

    public AdminUserView getUser(SessionUser requester, Long userId) {
        SysUser user = getAccessibleUser(requester, userId);
        return toViews(List.of(user)).stream()
                .findFirst()
                .orElseThrow(() -> new BizException(404, "后台用户不存在"));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createUser(AdminUserCreateCommand command) {
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        String username = normalizeRequiredText(command.getUsername(), "用户名");
        String displayName = normalizeRequiredText(command.getDisplayName(), "显示名称");
        assertUniqueUsername(username, null);
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(command.getPassword()));
        user.setDisplayName(displayName);
        user.setDeptId(command.getDeptId());
        user.setDeptAncestors(normalizeOptionalText(command.getDeptAncestors()));
        user.setStatus(resolveStatus(command.getStatus()));
        if (sysUserMapper.insert(user) != 1) {
            throw new BizException(500, "后台用户创建失败");
        }
        syncUserRoles(user.getId(), command.getRoleIds());
        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, AdminUserUpdateCommand command) {
        validatePositiveId(userId, "用户ID");
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        SysUser user = getRequiredUser(userId);
        String username = normalizeRequiredText(command.getUsername(), "用户名");
        String displayName = normalizeRequiredText(command.getDisplayName(), "显示名称");
        assertUniqueUsername(username, userId);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setDeptId(command.getDeptId());
        user.setDeptAncestors(normalizeOptionalText(command.getDeptAncestors()));
        user.setStatus(resolveStatus(command.getStatus()));
        if (StringUtils.hasText(command.getPassword())) {
            user.setPassword(passwordEncoder.encode(command.getPassword()));
        }
        if (sysUserMapper.updateById(user) != 1) {
            throw new BizException(500, "后台用户更新失败");
        }
        syncUserRoles(userId, command.getRoleIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer status) {
        validatePositiveId(userId, "用户ID");
        validateStatus(status, "用户状态");
        SysUser user = getRequiredUser(userId);
        user.setStatus(status);
        if (sysUserMapper.updateById(user) != 1) {
            throw new BizException(500, "后台用户状态更新失败");
        }
    }

    private List<AdminUserView> toViews(List<SysUser> users) {
        if (CollectionUtils.isEmpty(users)) {
            return List.of();
        }
        List<Long> userIds = users.stream().map(SysUser::getId).toList();
        List<SysUserRole> relations = sysUserRoleMapper.selectList(Wrappers.lambdaQuery(SysUserRole.class)
                .in(SysUserRole::getUserId, userIds));
        Map<Long, List<SysUserRole>> relationMap = relations.stream()
                .collect(Collectors.groupingBy(SysUserRole::getUserId));
        List<Long> roleIds = relations.stream()
                .map(SysUserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, SysRole> roleMap = CollectionUtils.isEmpty(roleIds) ? Map.of()
                : sysRoleMapper.selectBatchIds(roleIds).stream()
                .filter(Objects::nonNull)
                .filter(role -> role.getDeleted() == null || role.getDeleted() == 0)
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));
        return users.stream().map(user -> {
            List<SysRole> roles = relationMap.getOrDefault(user.getId(), List.of()).stream()
                    .map(item -> roleMap.get(item.getRoleId()))
                    .filter(Objects::nonNull)
                    .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
                    .toList();
            return AdminUserView.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .displayName(user.getDisplayName())
                    .userType(UserType.ADMIN)
                    .deptId(user.getDeptId())
                    .status(user.getStatus())
                    .roleIds(roles.stream().map(SysRole::getId).toList())
                    .roleKeys(roles.stream().map(SysRole::getRoleKey).toList())
                    .roleNames(roles.stream().map(SysRole::getRoleName).toList())
                    .build();
        }).toList();
    }

    private SysUser getAccessibleUser(SessionUser requester, Long userId) {
        validatePositiveId(userId, "用户ID");
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class);
        wrapper.eq(SysUser::getDeleted, 0)
                .eq(SysUser::getId, userId);
        dataScopeSqlSupport.apply(wrapper, requester, SysUser::getDeptId, SysUser::getId, SysUser::getDeptAncestors);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new BizException(404, "后台用户不存在");
        }
        return user;
    }

    private SysUser getRequiredUser(Long userId) {
        SysUser user = sysUserMapper.selectByUserId(userId);
        if (user == null) {
            throw new BizException(404, "后台用户不存在");
        }
        return user;
    }

    private void syncUserRoles(Long userId, List<Long> roleIds) {
        validatePositiveId(userId, "用户ID");
        List<Long> normalizedRoleIds = normalizeIds(roleIds, "角色ID");
        ensureRolesExist(normalizedRoleIds);
        sysUserRoleMapper.delete(Wrappers.lambdaQuery(SysUserRole.class)
                .eq(SysUserRole::getUserId, userId));
        for (Long roleId : normalizedRoleIds) {
            SysUserRole relation = new SysUserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            if (sysUserRoleMapper.insert(relation) != 1) {
                throw new BizException(500, "后台用户角色关联保存失败");
            }
        }
    }

    private void assertUniqueUsername(String username, Long excludeUserId) {
        SysUser existing = sysUserMapper.selectByUsername(username);
        if (existing != null && !Objects.equals(existing.getId(), excludeUserId)) {
            throw new BizException(400, "用户名已存在");
        }
    }

    private void ensureRolesExist(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds).stream()
                .filter(Objects::nonNull)
                .filter(role -> role.getDeleted() == null || role.getDeleted() == 0)
                .toList();
        if (roles.size() != roleIds.size()) {
            throw new BizException(400, "存在无效角色");
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

    private Integer resolveStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        validateStatus(status, "用户状态");
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
