package com.shoopping.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 令牌工具类
 * 负责生成、解析、校验 JWT 令牌
 *
 * @author shopping-team
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 根据 UserDetails 生成 JWT 令牌
     *
     * @param userDetails 用户信息
     * @return JWT 字符串
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration() * 1000);

        return Jwts.builder()
                // 主题：用户名
                .subject(userDetails.getUsername())
                // 签发时间
                .issuedAt(now)
                // 过期时间
                .expiration(expiry)
                // 令牌唯一标识（用于登出黑名单）
                .id(UUID.randomUUID().toString())
                // 签名
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中提取用户名
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT 令牌
     * @return true=有效, false=无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从令牌中提取 JTI（令牌唯一标识）
     *
     * @param token JWT 令牌
     * @return JTI 字符串
     */
    public String getJtiFromToken(String token) {
        return parseClaims(token).getId();
    }

    /**
     * 解析令牌并返回 Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取签名密钥（从 Base64 配置解码）
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
