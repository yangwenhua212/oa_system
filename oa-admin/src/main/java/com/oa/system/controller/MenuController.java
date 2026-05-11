package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.Menu;
import com.oa.system.security.RequirePerms;
import com.oa.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    @RequirePerms("system:menu:list")
    public R tree() {
        List<Menu> tree = menuService.getMenuTree();
        return R.ok(tree);
    }

    @GetMapping("/user")
    public R getUserMenu() {
        List<Menu> menus = menuService.getUserMenuTree();
        return R.ok(menus);
    }

    @GetMapping("/list")
    @RequirePerms("system:menu:list")
    public R list() {
        List<Menu> list = menuService.getMenuList();
        return R.ok(list);
    }

    @GetMapping("/{id}")
    @RequirePerms("system:menu:list")
    public R getById(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        return R.ok(menu);
    }

    @PostMapping
    @RequirePerms("system:menu:list")
    public R add(@RequestBody Menu menu) {
        Long id = menuService.addMenu(menu);
        return R.ok(id);
    }

    @PutMapping
    @RequirePerms("system:menu:list")
    public R update(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePerms("system:menu:list")
    public R delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
