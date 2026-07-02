package com.shoopping.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shoopping.common.Result;
import com.shoopping.model.product.ProductDetailVO;
import com.shoopping.model.product.ProductQueryParam;
import com.shoopping.model.product.ProductVO;
import com.shoopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品公开接口（无需认证）
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** 公开商品列表（仅上架，支持排序和筛选） */
    @GetMapping
    public Result<Page<ProductVO>> list(ProductQueryParam param) {
        return Result.success(productService.getPublicProductPage(param));
    }

    /** 公开商品详情 */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        ProductDetailVO vo = productService.getProductDetail(id);
        // 公开接口仅能查看上架商品
        if (vo.getStatus() == null || vo.getStatus() != 1) {
            return Result.error(400, "商品不存在或已下架");
        }
        return Result.success(vo);
    }
}
