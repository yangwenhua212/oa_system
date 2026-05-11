package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Role;
import com.oa.system.security.RequirePerms;
import com.oa.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    @RequirePerms("system:role:list")
    public R list(PageDTO<Role> pageDTO) {
        PageR<Role> result = roleService.getRolePage(pageDTO);
        return R.ok(result);
    }

    @GetMapping("/all")
    @RequirePerms({"system:role:list", "system:user:list"})
    public R getAll() {
        List<Role> roles = roleService.getAllRoles();
        return R.ok(roles);
    }

    @GetMapping("/my")
    public R getMyRoles() {
        List<Role> roles = roleService.getCurrentUserRoles();
        return R.ok(roles);
    }

    @GetMapping("/{id}")
    @RequirePerms("system:role:list")
    public R getById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return R.ok(role);
    }

    @PostMapping
    @RequirePerms("system:role:list")
    public R add(@RequestBody Role role) {
        Long id = roleService.addRole(role);
        return R.ok(id);
    }

    @PutMapping
    @RequirePerms("system:role:list")
    public R update(@RequestBody Role role) {
        roleService.updateRole(role);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePerms("system:role:list")
    public R delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }
}
