package com.shoopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类 - 对应 role 表
 *
 * @author shopping-team
 */
@Data
@TableName("role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称（显示用，如"系统管理员"） */
    private String roleName;

    /** 角色编码（如 ROLE_ADMIN，Spring Security hasRole 校验） */
    private String roleCode;

    /** 角色描述 */
    private String description;

    /** 状态: 0=禁用, 1=启用 */
    private Integer status;

    /** 排序号 */
    private Integer sort;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除: 0=正常, 1=已删除 */
    @TableLogic
    private Integer deleted;
}
