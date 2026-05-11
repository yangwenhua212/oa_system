package com.oa.system.security;

import com.oa.system.entity.User;
import com.oa.system.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT Authentication Filter
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 在线用户集合：userId -> token过期时间
    private static final ConcurrentHashMap<Long, Date> ONLINE_USERS = new ConcurrentHashMap<>();

    public static boolean isOnline(Long userId) {
        Date expiry = ONLINE_USERS.get(userId);
        return expiry != null && expiry.after(new Date());
    }

    public static void removeOnline(Long userId) {
        ONLINE_USERS.remove(userId);
    }

    public static ConcurrentHashMap<Long, Date> getOnlineUsers() {
        // 清理过期记录
        Date now = new Date();
        ONLINE_USERS.entrySet().removeIf(e -> e.getValue() == null || !e.getValue().after(now));
        return ONLINE_USERS;
    }

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User user = userService.getUserByUsername(username);

            if (user != null && user.getStatus() == 1) {
                // 从Token中获取过期时间
                Date expiration = jwtTokenUtil.getExpirationFromToken(token);
                // 标记在线（用token过期时间判断）
                if (expiration != null && expiration.after(new Date())) {
                    ONLINE_USERS.put(user.getId(), expiration);
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}