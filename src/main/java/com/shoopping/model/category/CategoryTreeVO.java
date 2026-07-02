package com.shoopping.model.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类树形视图（一级节点嵌套二级子节点）
 *
 * @author shopping-team
 */
@Data
public class CategoryTreeVO {

    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;

    /** 子分类列表（二级节点为空列表） */
    private List<CategoryTreeVO> children = new ArrayList<>();
}
