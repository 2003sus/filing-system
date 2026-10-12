package com.example.filing.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从 Header 取 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 如果是 "Bearer xxx" 格式，取出 token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 3. 验证 token
                Claims claims = jwtUtil.parse(token);

                // 4. 取出用户信息
                String userId = claims.getSubject();
                String username = claims.get("username", String.class);
                String role = claims.get("role", String.class);

                // 5. 放进 Spring Security 上下文
                //    Spring Security 要求权限名前缀 "ROLE_"
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,                              // principal（用户标识）
                                null,                                // credentials
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))  // 权限
                        );
                auth.setDetails(username);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // token 无效/过期，不设置认证，后续 Security 会拒绝
                System.out.println("JWT验证失败: " + e.getMessage());
            }
        }

        // 6. 继续走后面的过滤器
        filterChain.doFilter(request, response);
    }
}