package com.shoopping.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shoopping.entity.Product;
import com.shoopping.model.product.ProductCreateRequest;
import com.shoopping.model.product.ProductDetailVO;
import com.shoopping.model.product.ProductQueryParam;
import com.shoopping.model.product.ProductUpdateRequest;
import com.shoopping.model.product.ProductVO;

/**
 * 商品业务接口
 *
 * @author shopping-team
 */
public interface ProductService extends IService<Product> {

    /**
     * 公开商品分页列表（仅上架商品）
     */
    Page<ProductVO> getPublicProductPage(ProductQueryParam param);

    /**
     * 管理后台商品分页列表（含下架商品）
     */
    Page<ProductVO> getAdminProductPage(ProductQueryParam param);

    /**
     * 商品详情（含描述和副图）
     */
    ProductDetailVO getProductDetail(Long id);

    /**
     * 创建商品
     */
    Long createProduct(ProductCreateRequest request);

    /**
     * 更新商品
     */
    void updateProduct(Long id, ProductUpdateRequest request);

    /**
     * 上下架切换
     */
    void setProductStatus(Long id, Integer status);

    /**
     * 删除商品（逻辑删除）
     */
    void deleteProduct(Long id);
}
