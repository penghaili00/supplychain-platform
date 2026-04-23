package com.supplychain.service.provider.rbac.dubbo;

import com.supplychain.service.api.rbac.menu.command.MenuCreateCommand;
import com.supplychain.service.api.rbac.menu.command.MenuUpdateCommand;
import com.supplychain.service.api.rbac.menu.dubbo.AdminMenuDubboService;
import com.supplychain.service.api.rbac.menu.query.MenuQuery;
import com.supplychain.service.api.rbac.menu.view.MenuView;
import com.supplychain.service.provider.rbac.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
public class AdminMenuDubboServiceImpl implements AdminMenuDubboService {

    private final AdminMenuService adminMenuService;

    @Override
    public List<MenuView> listMenus(MenuQuery query) {
        return adminMenuService.listMenus(query);
    }

    @Override
    public MenuView getMenu(Long menuId) {
        return adminMenuService.getMenu(menuId);
    }

    @Override
    public Long createMenu(MenuCreateCommand command) {
        return adminMenuService.createMenu(command);
    }

    @Override
    public void updateMenu(Long menuId, MenuUpdateCommand command) {
        adminMenuService.updateMenu(menuId, command);
    }

    @Override
    public void updateMenuStatus(Long menuId, Integer status) {
        adminMenuService.updateMenuStatus(menuId, status);
    }
}
