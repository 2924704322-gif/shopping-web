package com.shoopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体类 - 对应 category 表
 * 采用邻接表模型，parent_id 为 NULL 表示一级分类，支持两级树形结构
 *
 * @author shopping-team
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 父分类ID（NULL = 一级分类） */
    private Long parentId;

    /** 层级: 1=一级, 2=二级 */
    private Integer level;

    /** 同级排序号 */
    private Integer sort;

    /** 图标URL */
    private String icon;

    /** 状态: 0=禁用, 1=启用 */
    private Integer status;

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
