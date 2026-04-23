package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.service.api.rbac.menu.command.MenuCreateCommand;
import com.supplychain.service.api.rbac.menu.command.MenuStatusCommand;
import com.supplychain.service.api.rbac.menu.command.MenuUpdateCommand;
import com.supplychain.service.api.rbac.menu.dubbo.AdminMenuDubboService;
import com.supplychain.service.api.rbac.menu.query.MenuQuery;
import com.supplychain.service.api.rbac.menu.view.MenuView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminMenuController {

    @DubboReference(check = false)
    private AdminMenuDubboService adminMenuDubboService;

    @GetMapping("/admin/menus")
    @RequirePermission("sys:menu:list")
    @OperationLog(title = "查询菜单权限列表", businessType = OperationType.QUERY)
    public R<List<MenuView>> list(MenuQuery query) {
        return R.ok(adminMenuDubboService.listMenus(query));
    }

    @GetMapping("/admin/menus/{menuId}")
    @RequirePermission("sys:menu:list")
    @OperationLog(title = "查询菜单权限详情", businessType = OperationType.QUERY)
    public R<MenuView> detail(@PathVariable Long menuId) {
        return R.ok(adminMenuDubboService.getMenu(menuId));
    }

    @PostMapping("/admin/menus")
    @RequirePermission("sys:menu:create")
    @OperationLog(title = "创建菜单权限", businessType = OperationType.CREATE)
    public R<Long> create(@Valid @RequestBody MenuCreateCommand command) {
        return R.ok(adminMenuDubboService.createMenu(command));
    }

    @PutMapping("/admin/menus/{menuId}")
    @RequirePermission("sys:menu:update")
    @OperationLog(title = "更新菜单权限", businessType = OperationType.UPDATE)
    public R<Void> update(@PathVariable Long menuId, @Valid @RequestBody MenuUpdateCommand command) {
        adminMenuDubboService.updateMenu(menuId, command);
        return R.ok(null);
    }

    @PutMapping("/admin/menus/{menuId}/status")
    @RequirePermission("sys:menu:update")
    @OperationLog(title = "更新菜单权限状态", businessType = OperationType.UPDATE)
    public R<Void> updateStatus(@PathVariable Long menuId, @Valid @RequestBody MenuStatusCommand command) {
        adminMenuDubboService.updateMenuStatus(menuId, command.getStatus());
        return R.ok(null);
    }
}
