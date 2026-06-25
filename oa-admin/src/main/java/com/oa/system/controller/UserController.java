package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.User;
import com.oa.system.security.JwtAuthenticationFilter;
import com.oa.system.security.RequirePerms;
import com.oa.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @RequirePerms("system:user:list")
    public R list(PageDTO<User> pageDTO) {
        PageR<User> result = userService.getUserPage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取所有用户（用于下拉选择）
     */
    @GetMapping("/all")
    @RequirePerms("system:user:list")
    public R getAllUsers() {
        List<User> users = userService.getAllUsers();
        return R.ok(users);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    @RequirePerms("system:user:list")
    public R getById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return R.ok(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @RequirePerms("system:user:list")
    public R add(@RequestBody User user) {
        Long id = userService.addUser(user);
        return R.ok(id);
    }

    /**
     * 修改用户
     */
    @PutMapping
    @RequirePerms("system:user:list")
    public R update(@RequestBody User user) {
        userService.updateUser(user);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @RequirePerms("system:user:list")
    public R delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword/{id}")
    @RequirePerms("system:user:list")
    public R resetPassword(@PathVariable Long id) {
        userService.resetPassword(id, "abc123456");
        return R.ok();
    }

    /**
     * 获取在线用户列表（基于JWT Token实时判断）
     */
    @GetMapping("/online")
    @RequirePerms("system:user:list")
    public R getOnlineUsers() {
        List<User> allUsers = userService.getAllUsers();
        List<User> onlineUsers = allUsers.stream()
                .filter(u -> JwtAuthenticationFilter.isOnline(u.getId()))
                .collect(Collectors.toList());
        return R.ok(onlineUsers);
    }

    /**
     * 获取部门下所有用户
     */
    @GetMapping("/dept/{deptId}")
    public R getUsersByDept(@PathVariable Long deptId) {
        List<User> users = userService.getUsersByDept(deptId);
        return R.ok(users);
    }
}
