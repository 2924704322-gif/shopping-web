package com.shoopping.service;

import com.shoopping.entity.User;
import com.shoopping.model.user.UserCreateRequest;
import com.shoopping.model.user.UserQueryParam;
import com.shoopping.model.user.UserUpdateRequest;
import com.shoopping.model.user.UserVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("创建用户 - 密码应被 BCrypt 加密")
    void shouldCreateUserWithEncodedPassword() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser1");
        request.setPassword("123456");
        request.setNickname("Test User");

        Long id = userService.createUser(request);
        assertNotNull(id);

        User saved = userService.getById(id);
        assertNotNull(saved);
        // BCrypt 加密后的密码以 $2 开头
        assertTrue(saved.getPassword().startsWith("$2"));
        // 不能与原始密码相同
        assertNotEquals("123456", saved.getPassword());
    }

    @Test
    @DisplayName("创建用户 - 重复用户名应抛出异常")
    void shouldRejectDuplicateUsername() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("admin");  // 预置用户
        request.setPassword("123456");

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));
    }

    @Test
    @DisplayName("删除用户 - 不能删除自己")
    void shouldRejectDeleteSelf() {
        // admin 用户 ID=1
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(1L, 1L));
    }

    @Test
    @DisplayName("禁用用户 - 不能禁用自己")
    void shouldRejectDisableSelf() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.setUserStatus(1L, 0, 1L));
    }

    @Test
    @DisplayName("分页查询 - 返回正确结构")
    void shouldReturnPagedUsers() {
        UserQueryParam param = new UserQueryParam();
        param.setPageNum(1);
        param.setPageSize(5);

        var page = userService.getUserPage(param);
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
        for (UserVO vo : page.getRecords()) {
            assertNotNull(vo.getUsername());
            // UserVO 不含密码字段
        }
    }

    @Test
    @DisplayName("分配角色 - 用户角色正确更新")
    void shouldAssignRoles() {
        // 创建测试用户
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("roletest");
        request.setPassword("123456");
        Long userId = userService.createUser(request);

        // 分配角色
        userService.assignRoles(userId, List.of(1L, 2L));

        UserVO vo = userService.getUserById(userId);
        assertNotNull(vo.getRoles());
        assertEquals(2, vo.getRoles().size());
        assertTrue(vo.getRoles().contains("ROLE_ADMIN"));
        assertTrue(vo.getRoles().contains("ROLE_USER"));
    }

    @Test
    @DisplayName("更新用户 - 正常更新不抛异常")
    void shouldUpdateUserSuccessfully() {
        // 创建新用户用于测试更新
        UserCreateRequest createReq = new UserCreateRequest();
        createReq.setUsername("updatetest");
        createReq.setPassword("123456");
        Long id = userService.createUser(createReq);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("Updated Nickname");

        assertDoesNotThrow(() -> userService.updateUser(id, request));

        UserVO vo = userService.getUserById(id);
        assertEquals("Updated Nickname", vo.getNickname());
    }
}
