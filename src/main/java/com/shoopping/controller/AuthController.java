package com.shoopping.controller;

import com.shoopping.common.Result;
import com.shoopping.model.LoginRequest;
import com.shoopping.model.LoginResponse;
import com.shoopping.security.JwtProperties;
import com.shoopping.security.JwtTokenProvider;
import com.shoopping.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 提供登录和登出接口
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户登录
     * 校验用户名密码，返回 JWT 令牌和用户信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. 执行认证（内部调用 UserDetailsServiceImpl.loadUserByUsername）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        // 2. 获取认证后的用户信息
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3. 生成 JWT
        String token = jwtTokenProvider.generateToken(userDetails);

        // 4. 构建响应
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .nickname(userDetails.getNickname())
                        .roles(userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                        .build())
                .build();

        return Result.success("登录成功", response);
    }

    /**
     * 用户登出
     * 将当前 token 加入 Redis 黑名单，使其失效
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            String token = authHeader.substring(jwtProperties.getTokenPrefix().length());
            try {
                String jti = jwtTokenProvider.getJtiFromToken(token);
                // 黑名单 TTL = token 剩余有效时间（token 过期后自动清理）
                redisTemplate.opsForValue().set(
                        "jwt:blacklist:" + jti,
                        "logout",
                        jwtProperties.getExpiration(),
                        TimeUnit.SECONDS);
            } catch (Exception e) {
                // Redis 不可用时忽略黑名单（客户端丢弃 token 即登出）
            }
        }
        return Result.success("已登出", null);
    }
}
