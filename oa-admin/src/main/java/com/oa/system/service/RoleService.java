package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     */
    PageR<Role> getRolePage(PageDTO<Role> pageDTO);

    /**
     * 根据ID查询角色
     */
    Role getRoleById(Long id);

    /**
     * 新增角色
     */
    Long addRole(Role role);

    /**
     * 修改角色
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 获取所有角色（用于下拉选择）
     */
    List<Role> getAllRoles();

    /**
     * 获取当前用户的角色列表
     */
    List<Role> getCurrentUserRoles();
}
