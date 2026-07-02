package com.shoopping.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应 DTO
 * 包含 JWT 令牌和用户基本信息
 *
 * @author shopping-team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** JWT 令牌 */
    private String token;

    /** 令牌类型：Bearer */
    private String tokenType;

    /** 过期时间（秒） */
    private Long expiresIn;

    /** 用户信息 */
    private UserInfo user;

    /**
     * 内嵌用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        /** 角色编码列表，如 ["ROLE_ADMIN", "ROLE_USER"] */
        private List<String> roles;
    }
}
