package com.shoopping.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shoopping.common.Result;
import com.shoopping.model.product.ProductCreateRequest;
import com.shoopping.model.product.ProductDetailVO;
import com.shoopping.model.product.ProductQueryParam;
import com.shoopping.model.product.ProductUpdateRequest;
import com.shoopping.model.product.ProductVO;
import com.shoopping.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品管理控制器（管理员）
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    /** 管理分页列表（含下架商品） */
    @GetMapping
    public Result<Page<ProductVO>> list(ProductQueryParam param) {
        return Result.success(productService.getAdminProductPage(param));
    }

    /** 管理详情 */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    /** 创建商品 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductCreateRequest request) {
        return Result.success("创建成功", productService.createProduct(request));
    }

    /** 更新商品 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(id, request);
        return Result.success("更新成功", null);
    }

    /** 上下架切换 */
    @PutMapping("/{id}/status")
    public Result<Void> setStatus(@PathVariable Long id,
                                  @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error(400, "状态值无效");
        }
        productService.setProductStatus(id, status);
        return Result.success("操作成功", null);
    }

    /** 删除商品 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功", null);
    }
}
