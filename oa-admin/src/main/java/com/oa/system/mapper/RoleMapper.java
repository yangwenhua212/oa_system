package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询角色列表
     */
    List<Role> selectRoleList(Role role);

    /**
     * 根据用户ID查询角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID查询权限标识列表
     */
    List<String> selectMenuPermsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     */
    void batchInsertUserRole(@Param("userId") Long userId, @Param("roleIds") Long[] roleIds);

    /**
     * 删除用户角色关联
     */
    void deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量插入角色菜单关联
     */
    void batchInsertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") Long[] menuIds);

    /**
     * 删除角色菜单关联
     */
    void deleteRoleMenuByRoleId(@Param("roleId") Long roleId);
}
