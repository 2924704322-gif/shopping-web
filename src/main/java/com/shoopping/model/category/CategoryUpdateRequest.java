package com.shoopping.model.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类更新请求
 *
 * @author shopping-team
 */
@Data
public class CategoryUpdateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称最长50字符")
    private String name;

    /** 父分类ID（null=一级分类） */
    private Long parentId;

    /** 同级排序号 */
    private Integer sort = 0;

    /** 图标URL */
    private String icon;
}
