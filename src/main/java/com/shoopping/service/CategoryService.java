package com.shoopping.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shoopping.entity.Category;
import com.shoopping.model.category.CategoryCreateRequest;
import com.shoopping.model.category.CategoryTreeVO;
import com.shoopping.model.category.CategoryUpdateRequest;
import com.shoopping.model.common.PageParam;

import java.util.List;

/**
 * 分类业务接口
 *
 * @author shopping-team
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树（公开，仅启用状态）
     * 一级节点嵌套二级子节点
     */
    List<CategoryTreeVO> getCategoryTree();

    /**
     * 分类分页列表（管理后台）
     */
    Page<Category> getCategoryPage(PageParam param);

    /**
     * 创建分类（自动设置 level）
     */
    Long createCategory(CategoryCreateRequest request);

    /**
     * 更新分类
     */
    void updateCategory(Long id, CategoryUpdateRequest request);

    /**
     * 删除分类（检查子分类和关联商品）
     */
    void deleteCategory(Long id);

    /**
     * 设置分类启用/禁用状态
     */
    void setCategoryStatus(Long id, Integer status);
}
