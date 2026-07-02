package com.shoopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoopping.entity.Category;
import com.shoopping.entity.Product;
import com.shoopping.mapper.CategoryMapper;
import com.shoopping.mapper.ProductMapper;
import com.shoopping.model.category.CategoryCreateRequest;
import com.shoopping.model.category.CategoryTreeVO;
import com.shoopping.model.category.CategoryUpdateRequest;
import com.shoopping.model.common.PageParam;
import com.shoopping.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类业务实现
 *
 * @author shopping-team
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final ProductMapper productMapper;

    @Override
    @Cacheable(value = "categoryTree", unless = "#result == null || #result.isEmpty()")
    public List<CategoryTreeVO> getCategoryTree() {
        // 查询所有启用分类
        List<Category> allCategories = lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();

        // 按 parentId 分组
        Map<Long, List<Category>> childrenMap = allCategories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        // 组装树：一级节点 + 其子节点
        return allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> {
                    CategoryTreeVO vo = new CategoryTreeVO();
                    BeanUtils.copyProperties(c, vo);
                    List<Category> children = childrenMap.getOrDefault(c.getId(), new ArrayList<>());
                    vo.setChildren(children.stream().map(child -> {
                        CategoryTreeVO childVo = new CategoryTreeVO();
                        BeanUtils.copyProperties(child, childVo);
                        childVo.setChildren(new ArrayList<>());
                        return childVo;
                    }).collect(Collectors.toList()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<Category> getCategoryPage(PageParam param) {
        Page<Category> page = new Page<>(param.getPageNum(), param.getPageSize());
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getLevel)
                .orderByAsc(Category::getSort);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public Long createCategory(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setIcon(request.getIcon());

        if (request.getParentId() == null) {
            // 一级分类
            category.setParentId(null);
            category.setLevel(1);
        } else {
            // 二级分类：校验父分类存在且为一级
            Category parent = getById(request.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在");
            }
            if (parent.getLevel() != 1) {
                throw new IllegalArgumentException("父分类必须是一级分类，最多支持两级");
            }
            category.setParentId(request.getParentId());
            category.setLevel(2);
        }

        save(category);
        return category.getId();
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = getById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }

        category.setName(request.getName());
        category.setSort(request.getSort());
        category.setIcon(request.getIcon());

        if (request.getParentId() == null) {
            category.setParentId(null);
            category.setLevel(1);
        } else {
            Category parent = getById(request.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在");
            }
            if (parent.getLevel() != 1) {
                throw new IllegalArgumentException("父分类必须是一级分类");
            }
            category.setParentId(request.getParentId());
            category.setLevel(2);
        }

        updateById(category);
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }

        // 检查是否有子分类
        long childrenCount = lambdaQuery().eq(Category::getParentId, id).count();
        if (childrenCount > 0) {
            throw new IllegalArgumentException("该分类下存在子分类，请先删除子分类");
        }

        // 检查是否有关联商品
        long productCount = productMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                        .eq(Product::getCategoryId, id));
        if (productCount > 0) {
            throw new IllegalArgumentException("该分类下存在商品，请先移除商品");
        }

        removeById(id);
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public void setCategoryStatus(Long id, Integer status) {
        Category category = getById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        category.setStatus(status);
        updateById(category);
    }
}
