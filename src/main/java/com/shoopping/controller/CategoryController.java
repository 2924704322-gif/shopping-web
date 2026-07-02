package com.shoopping.controller;

import com.shoopping.common.Result;
import com.shoopping.model.category.CategoryTreeVO;
import com.shoopping.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类公开接口
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** 分类树（一级→二级嵌套，仅启用状态） */
    @GetMapping("/tree")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }
}
