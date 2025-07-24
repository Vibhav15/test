package com.halodoc.batavia.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.halodoc.batavia.entity.menu.MenuResponse;
import com.halodoc.batavia.filters.ApiCategoryFilter.ApiCategory;
import com.halodoc.batavia.service.MenuService;
import com.halodoc.batavia.service.UserService;
import com.halodoc.core.constants.ApiType;
import com.halodoc.core.constants.Vertical;

@RestController
@RequestMapping("api/v1/menus")
public class MenuApiController extends BaseApiController {

    @Autowired private MenuService menuService;
    @Autowired private UserService userService;

    @GetMapping
    @ApiCategory (value = ApiType.SIMPLE_LOOKUP, verticalName = Vertical.CORE)
    public MenuResponse listMenuByRole(){
        MenuResponse response = new MenuResponse();
        response.setMenuItems(menuService.getMenuList());
        return response;
    }
}
