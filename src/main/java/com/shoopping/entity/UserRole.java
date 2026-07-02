package com.shoopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类 - 对应 user_role 表
 * 注意：此表不使用逻辑删除，撤销角色即物理删除行
 *
 * @author shopping-team
 */
@Data
@TableName("user_role")
public class UserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（逻辑外键 → user.id） */
    private Long userId;

    /** 角色ID（逻辑外键 → role.id） */
    private Long roleId;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
