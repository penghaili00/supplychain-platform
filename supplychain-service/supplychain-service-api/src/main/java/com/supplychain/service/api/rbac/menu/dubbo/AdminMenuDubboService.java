package com.supplychain.service.api.rbac.menu.dubbo;

import com.supplychain.service.api.rbac.menu.command.MenuCreateCommand;
import com.supplychain.service.api.rbac.menu.command.MenuUpdateCommand;
import com.supplychain.service.api.rbac.menu.query.MenuQuery;
import com.supplychain.service.api.rbac.menu.view.MenuView;

import java.util.List;

public interface AdminMenuDubboService {

    List<MenuView> listMenus(MenuQuery query);

    MenuView getMenu(Long menuId);

    Long createMenu(MenuCreateCommand command);

    void updateMenu(Long menuId, MenuUpdateCommand command);

    void updateMenuStatus(Long menuId, Integer status);
}
