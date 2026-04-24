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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class AdminMenuService {

    private final SysMenuMapper sysMenuMapper;

    public List<MenuView> listMenus(MenuQuery query) {
        List<SysMenu> allMenus = sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenu.class)
                .eq(SysMenu::getDeleted, 0)
                .orderByAsc(SysMenu::getId));
        if (CollectionUtils.isEmpty(allMenus)) {
            return List.of();
        }
        Map<Long, SysMenu> menuMap = new LinkedHashMap<>();
        allMenus.forEach(menu -> menuMap.put(menu.getId(), menu));
        Predicate<SysMenu> predicate = buildFilter(query);
        Map<Long, SysMenu> includedMenus = new LinkedHashMap<>();
        for (SysMenu menu : allMenus) {
            if (!predicate.test(menu)) {
                continue;
            }
            SysMenu current = menu;
            while (current != null) {
                includedMenus.putIfAbsent(current.getId(), current);
                Long parentId = normalizeParentId(current.getParentId());
                current = parentId <= 0 ? null : menuMap.get(parentId);
            }
        }
        return buildMenuTree(includedMenus.values().stream()
                .sorted(Comparator.comparing(SysMenu::getId))
                .toList());
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
        menu.setParentId(resolveParentId(command.getParentId(), null));
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
        menu.setParentId(resolveParentId(command.getParentId(), menuId));
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
                .parentId(normalizeParentId(menu.getParentId()))
                .menuName(menu.getMenuName())
                .permissionCode(menu.getPermissionCode())
                .status(menu.getStatus())
                .children(new ArrayList<>())
                .build();
    }

    private List<MenuView> buildMenuTree(List<SysMenu> menus) {
        Map<Long, MenuView> viewMap = new LinkedHashMap<>();
        menus.forEach(menu -> viewMap.put(menu.getId(), toView(menu)));
        List<MenuView> roots = new ArrayList<>();
        for (SysMenu menu : menus) {
            MenuView current = viewMap.get(menu.getId());
            Long parentId = normalizeParentId(menu.getParentId());
            if (parentId > 0 && viewMap.containsKey(parentId)) {
                viewMap.get(parentId).getChildren().add(current);
                continue;
            }
            roots.add(current);
        }
        return roots;
    }

    private SysMenu getRequiredMenu(Long menuId) {
        validatePositiveId(menuId, "菜单ID");
        SysMenu menu = sysMenuMapper.selectById(menuId);
        if (menu == null || (menu.getDeleted() != null && menu.getDeleted() == 1)) {
            throw new BizException(404, "菜单权限不存在");
        }
        return menu;
    }

    private Predicate<SysMenu> buildFilter(MenuQuery query) {
        if (query == null) {
            return menu -> true;
        }
        if (query.getStatus() != null) {
            validateStatus(query.getStatus(), "菜单状态");
        }
        return menu -> {
            if (query.getStatus() != null && !Objects.equals(menu.getStatus(), query.getStatus())) {
                return false;
            }
            if (!StringUtils.hasText(query.getKeyword())) {
                return true;
            }
            return menu.getMenuName() != null && menu.getMenuName().contains(query.getKeyword())
                    || menu.getPermissionCode() != null && menu.getPermissionCode().contains(query.getKeyword());
        };
    }

    private Long resolveParentId(Long parentId, Long menuId) {
        long value = parentId == null ? 0L : parentId;
        if (value < 0) {
            throw new BizException(400, "父级菜单ID不合法");
        }
        if (menuId != null && Objects.equals(menuId, value)) {
            throw new BizException(400, "父级菜单不能选择自身");
        }
        if (value == 0L) {
            return 0L;
        }
        SysMenu parent = getRequiredMenu(value);
        if (menuId != null && Objects.equals(parent.getId(), menuId)) {
            throw new BizException(400, "父级菜单不能选择自身");
        }
        return parent.getId();
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null ? 0L : parentId;
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
