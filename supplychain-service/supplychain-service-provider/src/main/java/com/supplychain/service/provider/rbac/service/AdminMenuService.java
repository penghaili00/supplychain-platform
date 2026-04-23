package com.supplychain.service.provider.rbac.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.service.api.rbac.menu.command.MenuCreateCommand;
import com.supplychain.service.api.rbac.menu.command.MenuUpdateCommand;
import com.supplychain.service.api.rbac.menu.query.MenuQuery;
import com.supplychain.service.api.rbac.menu.view.MenuView;
import com.supplychain.service.provider.rbac.entity.SysMenu;
import com.supplychain.service.provider.rbac.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final SysMenuMapper sysMenuMapper;

    public List<MenuView> listMenus(MenuQuery query) {
        LambdaQueryWrapper<SysMenu> wrapper = Wrappers.lambdaQuery(SysMenu.class);
        wrapper.eq(SysMenu::getDeleted, 0);
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            wrapper.and(q -> q.like(SysMenu::getMenuName, query.getKeyword())
                    .or()
                    .like(SysMenu::getPermissionCode, query.getKeyword()));
        }
        if (query != null && query.getStatus() != null) {
            validateStatus(query.getStatus(), "菜单状态");
            wrapper.eq(SysMenu::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(SysMenu::getId);
        return sysMenuMapper.selectList(wrapper).stream()
                .map(this::toView)
                .toList();
    }

    public MenuView getMenu(Long menuId) {
        return toView(getRequiredMenu(menuId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuCreateCommand command) {
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        SysMenu menu = new SysMenu();
        menu.setMenuName(normalizeRequiredText(command.getMenuName(), "菜单名称"));
        menu.setPermissionCode(normalizePermissionCode(command.getPermissionCode(), null));
        menu.setStatus(resolveStatus(command.getStatus()));
        if (sysMenuMapper.insert(menu) != 1) {
            throw new BizException(500, "菜单权限创建失败");
        }
        return menu.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long menuId, MenuUpdateCommand command) {
        validatePositiveId(menuId, "菜单ID");
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        SysMenu menu = getRequiredMenu(menuId);
        menu.setMenuName(normalizeRequiredText(command.getMenuName(), "菜单名称"));
        menu.setPermissionCode(normalizePermissionCode(command.getPermissionCode(), menuId));
        menu.setStatus(resolveStatus(command.getStatus()));
        if (sysMenuMapper.updateById(menu) != 1) {
            throw new BizException(500, "菜单权限更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMenuStatus(Long menuId, Integer status) {
        validatePositiveId(menuId, "菜单ID");
        validateStatus(status, "菜单状态");
        SysMenu menu = getRequiredMenu(menuId);
        menu.setStatus(status);
        if (sysMenuMapper.updateById(menu) != 1) {
            throw new BizException(500, "菜单权限状态更新失败");
        }
    }

    private MenuView toView(SysMenu menu) {
        return MenuView.builder()
                .menuId(menu.getId())
                .menuName(menu.getMenuName())
                .permissionCode(menu.getPermissionCode())
                .status(menu.getStatus())
                .build();
    }

    private SysMenu getRequiredMenu(Long menuId) {
        validatePositiveId(menuId, "菜单ID");
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null || (menu.getDeleted() != null && menu.getDeleted() == 1)) {
            throw new BizException(404, "菜单权限不存在");
        }
        return menu;
    }

    private String normalizePermissionCode(String permissionCode, Long excludeMenuId) {
        String value = normalizeOptionalText(permissionCode);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        SysMenu existing = sysMenuMapper.selectOne(Wrappers.lambdaQuery(SysMenu.class)
                .eq(SysMenu::getPermissionCode, value)
                .eq(SysMenu::getDeleted, 0)
                .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), excludeMenuId)) {
            throw new BizException(400, "权限编码已存在");
        }
        return value;
    }

    private Integer resolveStatus(Integer status) {
        if (status == null) {
            return 1;
        }
        validateStatus(status, "菜单状态");
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
