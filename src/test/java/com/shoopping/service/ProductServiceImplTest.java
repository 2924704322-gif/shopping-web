package com.shoopping.service;

import com.shoopping.model.product.ProductCreateRequest;
import com.shoopping.model.product.ProductDetailVO;
import com.shoopping.model.product.ProductQueryParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务测试
 */
@SpringBootTest
@Transactional
class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private Long categoryId;

    @BeforeEach
    void setUp() {
        // 每个测试前创建一个测试分类
        var request = new com.shoopping.model.category.CategoryCreateRequest();
        request.setName("Test Category");
        categoryId = categoryService.createCategory(request);
    }

    @Test
    @DisplayName("创建商品 - 副图 List 应序列化为 JSON")
    void shouldSerializeSubImages() {
        ProductCreateRequest request = buildProductRequest(
                "Test Product", List.of("url1.jpg", "url2.jpg"));

        Long id = productService.createProduct(request);
        var product = productService.getById(id);

        assertNotNull(product);
        assertNotNull(product.getSubImages());
        // 验证 JSON 数组格式
        assertTrue(product.getSubImages().startsWith("["));
        assertTrue(product.getSubImages().contains("url1.jpg"));
    }

    @Test
    @DisplayName("商品详情 - 副图 JSON 应反序列化为 List")
    void shouldDeserializeSubImages() {
        ProductCreateRequest request = buildProductRequest(
                "SubImages Test", List.of("a.jpg", "b.jpg", "c.jpg"));

        Long id = productService.createProduct(request);
        ProductDetailVO detail = productService.getProductDetail(id);

        assertNotNull(detail.getSubImages());
        assertEquals(3, detail.getSubImages().size());
        assertEquals("a.jpg", detail.getSubImages().get(0));
    }

    @Test
    @DisplayName("创建商品 - 新商品销量应为 0")
    void shouldSetSalesToZero() {
        ProductCreateRequest request = buildProductRequest("Sales Test", null);
        Long id = productService.createProduct(request);
        var product = productService.getById(id);
        assertEquals(0, product.getSales());
    }

    @Test
    @DisplayName("设置状态 - 无效值应抛异常")
    void shouldRejectInvalidStatus() {
        ProductCreateRequest request = buildProductRequest("Status Test", null);
        Long id = productService.createProduct(request);

        assertThrows(IllegalArgumentException.class,
                () -> productService.setProductStatus(id, 2));
        assertThrows(IllegalArgumentException.class,
                () -> productService.setProductStatus(id, -1));
    }

    @Test
    @DisplayName("公开列表 - 仅返回上架商品")
    void shouldOnlyReturnOnShelfProducts() {
        // 创建上架商品
        ProductCreateRequest onShelf = buildProductRequest("On Shelf", null);
        onShelf.setStatus(1);
        productService.createProduct(onShelf);

        // 创建下架商品
        ProductCreateRequest offShelf = buildProductRequest("Off Shelf", null);
        offShelf.setStatus(0);
        productService.createProduct(offShelf);

        ProductQueryParam param = new ProductQueryParam();
        param.setPageNum(1);
        param.setPageSize(20);
        var page = productService.getPublicProductPage(param);

        for (var vo : page.getRecords()) {
            assertEquals(1, vo.getStatus(), "公开列表不应包含下架商品: " + vo.getName());
        }
    }

    @Test
    @DisplayName("分页查询 - 关键字搜索")
    void shouldSearchByKeyword() {
        productService.createProduct(buildProductRequest("UniqueKeyword XYZ", null));

        ProductQueryParam param = new ProductQueryParam();
        param.setKeyword("UniqueKeyword");
        var page = productService.getAdminProductPage(param);

        assertTrue(page.getTotal() >= 1);
    }

    private ProductCreateRequest buildProductRequest(String name, List<String> subImages) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName(name);
        request.setCategoryId(categoryId);
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(100);
        request.setSubImages(subImages);
        return request;
    }
}
