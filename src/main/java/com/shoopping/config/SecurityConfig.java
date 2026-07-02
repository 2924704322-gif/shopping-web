package com.shoopping.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoopping.common.Result;
import com.shoopping.security.JwtAuthenticationFilter;
import com.shoopping.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 安全配置
 * - JWT 无状态认证
 * - 基于路径的 RBAC 权限控制
 * - 自定义认证入口点和拒绝访问处理器（返回 JSON）
 *
 * @author shopping-team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 密码编码器：BCrypt 强哈希
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 认证提供器：使用数据库用户详情服务 + BCrypt 密码校验
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        // 不隐藏用户名查找失败异常（方便调试，生产可设为 true）
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（JWT 无状态，不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)
                // 无状态会话（不创建 HttpSession）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // === 公开接口 ===
                        // 登录接口
                        .requestMatchers("/api/auth/login").permitAll()
                        // 注册接口（预留）
                        .requestMatchers("/api/auth/register").permitAll()

                        // === 公开接口：静态资源 ===
                        .requestMatchers("/uploads/**").permitAll()

                        // === 公开接口：商品和分类浏览 ===
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // === 管理后台：仅 ADMIN ===
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // === 商家接口：SELLER 或 ADMIN ===
                        .requestMatchers("/api/seller/**").hasAnyRole("SELLER", "ADMIN")

                        // === 用户接口：需登录 ===
                        .requestMatchers("/api/user/**").authenticated()

                        // === 其余请求：需登录 ===
                        .anyRequest().authenticated()
                )
                // 异常处理：返回 JSON 而非重定向
                .exceptionHandling(ex -> ex
                        // 未登录（无 token / token 无效）
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            String json = new ObjectMapper().writeValueAsString(
                                    Result.error(401, "未登录或令牌已失效"));
                            response.getWriter().write(json);
                        })
                        // 权限不足
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            String json = new ObjectMapper().writeValueAsString(
                                    Result.error(403, "权限不足，无法访问该资源"));
                            response.getWriter().write(json);
                        })
                )
                // JWT 过滤器：在 UsernamePasswordAuthenticationFilter 之前执行
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
