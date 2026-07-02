package com.shoopping.common;

import lombok.Data;

/**
 * 统一响应结果类
 * 所有控制器接口均返回此结构，保证前端接收格式一致
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> {

    /** 业务状态码: 200=成功, 其他=失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    private Result() {
    }

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 成功响应 ==========

    /** 操作成功（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /** 操作成功（携带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /** 操作成功（自定义消息 + 数据） */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ========== 失败响应 ==========

    /** 操作失败 */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /** 操作失败（默认 500） */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
