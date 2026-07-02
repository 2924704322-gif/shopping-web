package com.shoopping.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoopping.common.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 认证过滤器
 * 拦截所有请求，从 Authorization 头提取并校验 JWT 令牌
 *
 * @author shopping-team
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 提取 Authorization 头
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            // 无 token，放行给 SecurityConfig 处理（由权限规则决定是否拒绝）
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取 token
        String token = authHeader.substring(jwtProperties.getTokenPrefix().length());

        // 3. 检查 Redis 黑名单（登出后的 token）
        String jti;
        try {
            jti = jwtTokenProvider.getJtiFromToken(token);
        } catch (Exception e) {
            // token 格式异常，无法解析 JTI
            sendError(response, HttpStatus.UNAUTHORIZED.value(), "令牌格式无效");
            return;
        }

        // 黑名单检查（Redis 不可用时跳过）
        try {
            String blacklistKey = "jwt:blacklist:" + jti;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                SecurityContextHolder.clearContext();
                sendError(response, HttpStatus.UNAUTHORIZED.value(), "令牌已失效，请重新登录");
                return;
            }
        } catch (Exception e) {
            log.warn("Redis 黑名单检查失败，跳过: {}", e.getMessage());
        }

        // 4. 校验 token
        if (!jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder.clearContext();
            sendError(response, HttpStatus.UNAUTHORIZED.value(), "令牌无效或已过期");
            return;
        }

        // 5. 加载用户信息并设置安全上下文
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("设置用户认证信息失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 向前端返回 JSON 格式的认证失败信息
     */
    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = new ObjectMapper().writeValueAsString(Result.error(code, message));
        response.getWriter().write(json);
    }
}
