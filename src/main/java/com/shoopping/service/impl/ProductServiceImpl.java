package com.shoopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoopping.entity.Category;
import com.shoopping.entity.Product;
import com.shoopping.mapper.ProductMapper;
import com.shoopping.model.product.ProductCreateRequest;
import com.shoopping.model.product.ProductDetailVO;
import com.shoopping.model.product.ProductQueryParam;
import com.shoopping.model.product.ProductUpdateRequest;
import com.shoopping.model.product.ProductVO;
import com.shoopping.service.CategoryService;
import com.shoopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品业务实现
 *
 * @author shopping-team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final CategoryService categoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<ProductVO> getPublicProductPage(ProductQueryParam param) {
        // 公开列表强制仅查询上架商品
        param.setStatus(1);
        return queryProductPage(param);
    }

    @Override
    public Page<ProductVO> getAdminProductPage(ProductQueryParam param) {
        return queryProductPage(param);
    }

    /**
     * 统一分页查询逻辑
     */
    private Page<ProductVO> queryProductPage(ProductQueryParam param) {
        Page<Product> page = new Page<>(param.getPageNum(), param.getPageSize());

        LambdaQueryWrapper<Product> wrapper = Wrappers.<Product>lambdaQuery();

        // 关键字搜索
        if (StringUtils.hasText(param.getKeyword())) {
            wrapper.like(Product::getName, param.getKeyword());
        }

        // 分类筛选
        if (param.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, param.getCategoryId());
        }

        // 上下架筛选
        if (param.getStatus() != null) {
            wrapper.eq(Product::getStatus, param.getStatus());
        }

        // 价格区间
        if (StringUtils.hasText(param.getPriceMin())) {
            wrapper.ge(Product::getPrice, new BigDecimal(param.getPriceMin()));
        }
        if (StringUtils.hasText(param.getPriceMax())) {
            wrapper.le(Product::getPrice, new BigDecimal(param.getPriceMax()));
        }

        // 排序
        String sortField = param.getSortField();
        if ("price".equals(sortField)) {
            wrapper.orderBy(true, param.isAsc(), Product::getPrice);
        } else if ("sales".equals(sortField)) {
            wrapper.orderBy(true, param.isAsc(), Product::getSales);
        } else {
            // 默认：sort 升序 + createTime 降序
            wrapper.orderByAsc(Product::getSort)
                   .orderByDesc(Product::getCreateTime);
        }

        Page<Product> productPage = baseMapper.selectPage(page, wrapper);

        // 批量获取分类名称
        Set<Long> categoryIds = productPage.getRecords().stream()
                .map(Product::getCategoryId)
                .collect(Collectors.toSet());
        Map<Long, String> categoryMap = categoryService.listByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));

        // 转换为 VO
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(product -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(product, vo);
            vo.setCategoryName(categoryMap.getOrDefault(product.getCategoryId(), "未知"));
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Cacheable(value = "productDetail", key = "#id", unless = "#result == null")
    public ProductDetailVO getProductDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 分类名称
        Category category = categoryService.getById(product.getCategoryId());
        vo.setCategoryName(category != null ? category.getName() : "未知");

        // 副图 JSON → List
        vo.setSubImages(parseSubImages(product.getSubImages()));

        return vo;
    }

    @Override
    public Long createProduct(ProductCreateRequest request) {
        // 校验分类存在
        Category category = categoryService.getById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("商品分类不存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        // 副图 List → JSON 字符串
        if (request.getSubImages() != null && !request.getSubImages().isEmpty()) {
            try {
                product.setSubImages(objectMapper.writeValueAsString(request.getSubImages()));
            } catch (Exception e) {
                throw new IllegalArgumentException("副图数据格式错误");
            }
        }

        // 新商品销量为 0
        product.setSales(0);

        save(product);
        return product.getId();
    }

    @Override
    @CacheEvict(value = "productDetail", key = "#id")
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        BeanUtils.copyProperties(request, product);

        // 副图 List → JSON
        if (request.getSubImages() != null && !request.getSubImages().isEmpty()) {
            try {
                product.setSubImages(objectMapper.writeValueAsString(request.getSubImages()));
            } catch (Exception e) {
                throw new IllegalArgumentException("副图数据格式错误");
            }
        } else if (request.getSubImages() != null) {
            product.setSubImages(null);
        }

        updateById(product);
    }

    @Override
    @CacheEvict(value = "productDetail", key = "#id")
    public void setProductStatus(Long id, Integer status) {
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("状态值无效，必须为 0 或 1");
        }
        Product product = getById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        product.setStatus(status);
        updateById(product);
    }

    @Override
    @CacheEvict(value = "productDetail", key = "#id")
    public void deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        // MyBatis-Plus @TableLogic 自动转为逻辑删除
        removeById(id);
    }

    /**
     * 解析副图 JSON 字符串为 List
     */
    private List<String> parseSubImages(String subImagesJson) {
        if (!StringUtils.hasText(subImagesJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(subImagesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("副图 JSON 解析失败: {}", subImagesJson);
            return Collections.emptyList();
        }
    }
}
