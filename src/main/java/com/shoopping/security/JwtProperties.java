package com.shoopping.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * 绑定 application.yml 中 jwt.* 配置项
 *
 * @author shopping-team
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC-SHA 签名密钥（Base64 编码，至少 256 bits） */
    private String secret;

    /** 令牌有效期（秒），默认 24 小时 */
    private long expiration = 86400L;

    /** Authorization 头令牌前缀 */
    private String tokenPrefix = "Bearer ";
}
