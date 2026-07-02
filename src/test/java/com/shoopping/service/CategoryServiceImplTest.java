package com.shoopping.service;

import com.shoopping.model.category.CategoryCreateRequest;
import com.shoopping.model.category.CategoryTreeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类服务测试
 */
@SpringBootTest
@Transactional
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("创建一级分类 - level 应为 1")
    void shouldAutoSetLevel1() {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Level1 Category");
        request.setParentId(null);

        Long id = categoryService.createCategory(request);
        var category = categoryService.getById(id);

        assertNotNull(category);
        assertEquals(1, category.getLevel());
        assertNull(category.getParentId());
    }

    @Test
    @DisplayName("创建二级分类 - level 应为 2")
    void shouldAutoSetLevel2() {
        // 先创建一级
        CategoryCreateRequest l1 = new CategoryCreateRequest();
        l1.setName("Parent Category");
        Long parentId = categoryService.createCategory(l1);

        // 创建二级
        CategoryCreateRequest l2 = new CategoryCreateRequest();
        l2.setName("Child Category");
        l2.setParentId(parentId);
        Long childId = categoryService.createCategory(l2);

        var child = categoryService.getById(childId);
        assertEquals(2, child.getLevel());
        assertEquals(parentId, child.getParentId());
    }

    @Test
    @DisplayName("分类树 - 应返回正确层级结构")
    void shouldBuildTree() {
        List<CategoryTreeVO> tree = categoryService.getCategoryTree();
        assertNotNull(tree);

        for (CategoryTreeVO node : tree) {
            assertEquals(1, node.getLevel());
            assertNull(node.getParentId());
            // 子节点存在时应有 children 列表
            assertNotNull(node.getChildren());
            for (CategoryTreeVO child : node.getChildren()) {
                assertEquals(2, child.getLevel());
            }
        }
    }

    @Test
    @DisplayName("删除分类 - 有子分类时抛异常")
    void shouldRejectDeleteWithChildren() {
        // 创建一级 + 二级
        CategoryCreateRequest l1 = new CategoryCreateRequest();
        l1.setName("Parent Del Test");
        Long parentId = categoryService.createCategory(l1);

        CategoryCreateRequest l2 = new CategoryCreateRequest();
        l2.setName("Child Del Test");
        l2.setParentId(parentId);
        categoryService.createCategory(l2);

        // 删除一级应失败
        Long finalParentId = parentId;
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.deleteCategory(finalParentId));
    }

    @Test
    @DisplayName("删除分类 - 无子分类时删除成功")
    void shouldDeleteLeafCategory() {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Leaf Category");
        Long id = categoryService.createCategory(request);

        assertDoesNotThrow(() -> categoryService.deleteCategory(id));
    }
}
