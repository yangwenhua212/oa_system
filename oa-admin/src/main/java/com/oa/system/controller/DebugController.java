package com.oa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.common.R;
import com.oa.system.entity.User;
import com.oa.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Debug Controller - For password reset only
 * TODO: Remove this controller in production
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Reset password for a user
     * Usage: POST http://localhost:8080/api/debug/reset-password
     * Body: {"username": "admin", "password": "admin123"}
     */
    @GetMapping("/reset-password")
    public R<Map<String, Object>> resetPassword(@RequestParam String username, @RequestParam String password) {
        
        if (username == null || password == null) {
            return R.error(400, "Username and password are required");
        }
        
        System.out.println("=== [DEBUG] Resetting password for user: " + username + " to: " + password);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            System.out.println("=== [DEBUG] User not found: " + username);
            return R.error(404, "User not found: " + username);
        }
        
        // Encode the new password
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("=== [DEBUG] Generated hash: " + encodedPassword);
        
        // Update password
        user.setPassword(encodedPassword);
        userMapper.updateById(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("newHash", encodedPassword);
        
        System.out.println("=== [DEBUG] Password updated successfully!");
        return R.success("Password reset successfully for user: " + username, result);
    }
    
    /**
     * Test password encoding
     * Usage: GET http://localhost:8080/api/debug/test-password?password=admin123
     */
    @GetMapping("/test-password")
    public R<Map<String, Object>> testPassword(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, hash);
        
        Map<String, Object> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", hash);
        result.put("matches", matches);
        
        System.out.println("=== [DEBUG] Password test - Password: " + password);
        System.out.println("=== [DEBUG] Hash: " + hash);
        System.out.println("=== [DEBUG] Matches: " + matches);
        
        return R.success(result);
    }
    
    /**
     * Check user status
     * Usage: GET http://localhost:8080/api/debug/check-user?username=admin
     */
    @GetMapping("/check-user")
    public R<Map<String, Object>> checkUser(@RequestParam String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            result.put("exists", false);
            return R.success(result);
        }
        
        result.put("exists", true);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("status", user.getStatus());
        result.put("passwordHash", user.getPassword());
        
        System.out.println("=== [DEBUG] User check - " + username);
        System.out.println("=== [DEBUG] User found: " + user.getUsername());
        System.out.println("=== [DEBUG] Status: " + user.getStatus());
        System.out.println("=== [DEBUG] Current hash: " + user.getPassword());
        
        return R.success(result);
    }
}
