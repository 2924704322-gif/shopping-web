package com.shoopping.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shoopping.entity.User;
import com.shoopping.mapper.UserMapper;
import com.shoopping.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 基于数据库的 UserDetailsService 实现
 * 从 user 表加载用户信息，从 user_role + role 表加载权限
 *
 * @author shopping-team
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户（MyBatis-Plus @TableLogic 自动过滤 deleted=1）
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 2. 查询用户的角色编码列表（JOIN 查询，一次请求获取所有角色）
        List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(user.getId());

        // 3. 构建 Spring Security UserDetails
        return new UserDetailsImpl(user, roleCodes);
    }
}
