package com.oa.system.service;

import com.oa.system.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 获取菜单树
     */
    List<Menu> getMenuTree();

    /**
     * 获取用户菜单树
     */
    List<Menu> getUserMenuTree();

    /**
     * 获取所有菜单列表
     */
    List<Menu> getMenuList();

    /**
     * 根据ID查询菜单
     */
    Menu getMenuById(Long id);

    /**
     * 新增菜单
     */
    Long addMenu(Menu menu);

    /**
     * 修改菜单
     */
    boolean updateMenu(Menu menu);

    /**
     * 删除菜单
     */
    boolean deleteMenu(Long id);
}
