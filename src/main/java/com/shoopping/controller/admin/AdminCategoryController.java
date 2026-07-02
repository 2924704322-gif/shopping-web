package com.shoopping.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shoopping.common.Result;
import com.shoopping.entity.Category;
import com.shoopping.model.category.CategoryCreateRequest;
import com.shoopping.model.category.CategoryUpdateRequest;
import com.shoopping.model.common.PageParam;
import com.shoopping.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 分类管理控制器（管理员）
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    /** 分页列表 */
    @GetMapping
    public Result<Page<Category>> list(PageParam param) {
        return Result.success(categoryService.getCategoryPage(param));
    }

    /** 分类详情 */
    @GetMapping("/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error(400, "分类不存在");
        }
        return Result.success(category);
    }

    /** 创建分类 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CategoryCreateRequest request) {
        return Result.success("创建成功", categoryService.createCategory(request));
    }

    /** 更新分类 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success("更新成功", null);
    }

    /** 删除分类 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/status")
    public Result<Void> setStatus(@PathVariable Long id,
                                  @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error(400, "状态值无效");
        }
        categoryService.setCategoryStatus(id, status);
        return Result.success("操作成功", null);
    }
}
