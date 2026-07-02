package com.shoopping.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户更新请求（不含用户名和密码）
 *
 * @author shopping-team
 */
@Data
public class UserUpdateRequest {

    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 性别: 0=未知, 1=男, 2=女 */
    private Integer gender;

    private LocalDate birthday;
}
