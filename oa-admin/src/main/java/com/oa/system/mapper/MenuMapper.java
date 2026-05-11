package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询菜单列表
     */
    List<Menu> selectMenuList(Menu menu);

    /**
     * 查询用户菜单树
     */
    List<Menu> selectMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单树
     */
    List<Menu> selectMenuTreeAll();

    /**
     * 查询菜单路由
     */
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户拥有的所有权限标识（跨角色取并集）
     */
    List<String> selectUserPerms(@Param("userId") Long userId);
}
