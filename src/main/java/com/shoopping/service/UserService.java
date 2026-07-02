package com.shoopping.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shoopping.entity.User;
import com.shoopping.model.user.UserCreateRequest;
import com.shoopping.model.user.UserQueryParam;
import com.shoopping.model.user.UserUpdateRequest;
import com.shoopping.model.user.UserVO;

import java.util.List;

/**
 * 用户业务接口
 *
 * @author shopping-team
 */
public interface UserService extends IService<User> {

    /**
     * 用户分页列表（含角色信息）
     */
    Page<UserVO> getUserPage(UserQueryParam param);

    /**
     * 用户详情（含角色）
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户（BCrypt 加密密码 + 唯一性校验）
     */
    Long createUser(UserCreateRequest request);

    /**
     * 更新用户信息（不含密码）
     */
    void updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户（禁止删除自己）
     */
    void deleteUser(Long id, Long currentUserId);

    /**
     * 设置用户状态（禁止禁用自己）
     */
    void setUserStatus(Long id, Integer status, Long currentUserId);

    /**
     * 分配角色（替换模式：先删后增）
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
