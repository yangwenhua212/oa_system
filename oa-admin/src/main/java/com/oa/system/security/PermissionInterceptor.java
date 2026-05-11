package com.oa.system.security;

import com.oa.system.entity.User;
import com.oa.system.mapper.MenuMapper;
import com.oa.system.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验拦截器
 * 检查 @RequirePerms 注解，当前用户无权限时返回 403
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;
    private final MenuMapper menuMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截 Controller 方法
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequirePerms requirePerms = handlerMethod.getMethodAnnotation(RequirePerms.class);
        if (requirePerms == null) {
            return true; // 方法没有权限注解，放行
        }

        // 获取当前用户
        String token = jwtTokenUtil.getTokenFromRequest();
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return false;
        }

        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
            return false;
        }

        // 管理员拥有所有权限
        if (user.getIsAdmin() != null && user.getIsAdmin() == 1) {
            return true;
        }

        // 获取用户实际拥有的权限
        List<String> userPerms = menuMapper.selectUserPerms(userId);
        Set<String> userPermSet = userPerms.stream()
                .filter(p -> p != null && !p.isEmpty())
                .collect(Collectors.toSet());

        // 检查是否拥有任一所需权限
        String[] requiredPerms = requirePerms.value();
        boolean hasPerm = Arrays.stream(requiredPerms)
                .anyMatch(userPermSet::contains);

        if (!hasPerm) {
            log.warn("用户 {} 缺少权限: {}", userId, Arrays.toString(requiredPerms));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问");
            return false;
        }

        return true;
    }
}
