package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Role;
import com.oa.system.mapper.RoleMapper;
import com.oa.system.service.RoleService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public PageR<Role> getRolePage(PageDTO<Role> pageDTO) {
        Page<Role> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Role params = pageDTO.getParams() != null ? pageDTO.getParams() : new Role();

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getRoleName() != null, Role::getRoleName, params.getRoleName())
                .eq(params.getStatus() != null, Role::getStatus, params.getStatus())
                .orderByDesc(Role::getCreateTime);

        Page<Role> result = roleMapper.selectPage(page, wrapper);
        // 填充菜单数量
        for (Role role : result.getRecords()) {
            List<Long> ids = roleMapper.selectMenuIdsByRoleId(role.getId());
            role.setMenuCount(ids != null ? ids.size() : 0);
        }
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role != null) {
            // 获取角色菜单ID列表
            List<Long> menuIds = roleMapper.selectMenuIdsByRoleId(id);
            role.setMenuIds(menuIds.toArray(new Long[0]));
        }
        return role;
    }

    @Override
    @Transactional
    public Long addRole(Role role) {
        roleMapper.insert(role);

        // 分配菜单权限
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            roleMapper.batchInsertRoleMenu(role.getId(), role.getMenuIds());
        }

        return role.getId();
    }

    @Override
    @Transactional
    public boolean updateRole(Role role) {
        // 检查是否有实际字段需要更新（排除 menuIds 这种虚拟字段）
        boolean hasFields = role.getRoleName() != null || role.getRoleCode() != null
                || role.getRemark() != null || role.getStatus() != null
                || role.getDataScope() != null || role.getRoleType() != null;

        if (hasFields) {
            // 有实际字段更新时调用 updateById
            roleMapper.updateById(role);
        }

        // 如果有菜单权限变更，单独处理角色菜单关联
        if (role.getMenuIds() != null) {
            roleMapper.deleteRoleMenuByRoleId(role.getId());
            if (role.getMenuIds().length > 0) {
                roleMapper.batchInsertRoleMenu(role.getId(), role.getMenuIds());
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long id) {
        if (id == 1) {
            throw new RuntimeException("不能删除超级管理员角色");
        }
        roleMapper.deleteRoleMenuByRoleId(id);
        roleMapper.deleteById(id);
        return true;
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getCreateTime);
        return roleMapper.selectList(wrapper);
    }

    @Override
    public List<Role> getCurrentUserRoles() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return roleMapper.selectRolesByUserId(userId);
    }
}
