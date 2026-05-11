package com.oa.system.controller;

import com.oa.system.common.R;
import com.oa.system.entity.User;
import com.oa.system.security.JwtAuthenticationFilter;
import com.oa.system.security.JwtTokenUtil;
import com.oa.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        User user = userService.login(username, password);

        // 生成Token
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return R.ok(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public R getUserInfo() {
        User user = userService.getCurrentUser();
        return R.ok(user);
    }

    /**
     * 获取当前用户菜单
     */
    @GetMapping("/menus")
    public R getMenus() {
        List menus = userService.getCurrentUserMenus();
        return R.ok(menus);
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public R changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        userService.changePassword(oldPassword, newPassword);
        return R.ok();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R logout() {
        // 移除在线状态
        String token = jwtTokenUtil.getTokenFromRequest();
        if (token != null) {
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            JwtAuthenticationFilter.removeOnline(userId);
        }
        return R.ok();
    }
}
