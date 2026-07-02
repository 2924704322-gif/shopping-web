package com.shoopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoopping.entity.User;
import com.shoopping.entity.UserRole;
import com.shoopping.mapper.UserMapper;
import com.shoopping.mapper.UserRoleMapper;
import com.shoopping.model.user.UserCreateRequest;
import com.shoopping.model.user.UserQueryParam;
import com.shoopping.model.user.UserUpdateRequest;
import com.shoopping.model.user.UserVO;
import com.shoopping.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户业务实现
 *
 * @author shopping-team
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserVO> getUserPage(UserQueryParam param) {
        Page<User> page = new Page<>(param.getPageNum(), param.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(StringUtils.hasText(param.getKeyword()), w -> w
                        .like(User::getUsername, param.getKeyword())
                        .or()
                        .like(User::getNickname, param.getKeyword())
                        .or()
                        .like(User::getEmail, param.getKeyword())
                        .or()
                        .like(User::getPhone, param.getKeyword()))
                .eq(param.getStatus() != null, User::getStatus, param.getStatus())
                .orderByDesc(User::getCreateTime);

        Page<User> userPage = baseMapper.selectPage(page, wrapper);

        // 转换为 VO 并填充角色
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            vo.setRoles(userRoleMapper.selectRoleCodesByUserId(user.getId()));
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRoles(userRoleMapper.selectRoleCodesByUserId(user.getId()));
        return vo;
    }

    @Override
    public Long createUser(UserCreateRequest request) {
        // 用户名唯一性校验
        if (lambdaQuery().eq(User::getUsername, request.getUsername()).count() > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 邮箱唯一性校验
        if (StringUtils.hasText(request.getEmail())
                && lambdaQuery().eq(User::getEmail, request.getEmail()).count() > 0) {
            throw new IllegalArgumentException("邮箱已被使用");
        }
        // 手机号唯一性校验
        if (StringUtils.hasText(request.getPhone())
                && lambdaQuery().eq(User::getPhone, request.getPhone()).count() > 0) {
            throw new IllegalArgumentException("手机号已被使用");
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        // BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        save(user);
        return user.getId();
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        User user = getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 邮箱唯一性校验（排除自身）
        if (StringUtils.hasText(request.getEmail())
                && lambdaQuery().eq(User::getEmail, request.getEmail())
                        .ne(User::getId, id).count() > 0) {
            throw new IllegalArgumentException("邮箱已被使用");
        }
        // 手机号唯一性校验
        if (StringUtils.hasText(request.getPhone())
                && lambdaQuery().eq(User::getPhone, request.getPhone())
                        .ne(User::getId, id).count() > 0) {
            throw new IllegalArgumentException("手机号已被使用");
        }

        BeanUtils.copyProperties(request, user);
        updateById(user);
    }

    @Override
    public void deleteUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new IllegalArgumentException("不能删除自己的账户");
        }
        User user = getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // @TableLogic 自动转为逻辑删除
        removeById(id);
    }

    @Override
    public void setUserStatus(Long id, Integer status, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new IllegalArgumentException("不能禁用自己的账户");
        }
        User user = getById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 先删除旧角色关联
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        // 再批量插入新角色
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }
}
