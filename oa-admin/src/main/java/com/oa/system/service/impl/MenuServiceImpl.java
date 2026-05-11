package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.entity.Menu;
import com.oa.system.mapper.MenuMapper;
import com.oa.system.service.MenuService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = menuMapper.selectMenuList(new Menu());
        return buildMenuTree(allMenus);
    }

    @Override
    public List<Menu> getUserMenuTree() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        List<Menu> menus = menuMapper.selectMenuTreeByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<Menu> getMenuList() {
        return menuMapper.selectMenuList(new Menu());
    }

    /**
     * 构建菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> result = new ArrayList<>();
        Map<Long, List<Menu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        for (Menu menu : menus) {
            if (menu.getParentId() == 0) {
                result.add(menu);
            }
            menu.setChildren(childrenMap.getOrDefault(menu.getId(), new ArrayList<>()));
        }

        return result;
    }

    @Override
    public Menu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long addMenu(Menu menu) {
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public boolean updateMenu(Menu menu) {
        menuMapper.updateById(menu);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteMenu(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, id);
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("存在下级菜单，无法删除");
        }
        menuMapper.deleteById(id);
        return true;
    }
}
