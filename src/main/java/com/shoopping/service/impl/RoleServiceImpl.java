package com.shoopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoopping.entity.Role;
import com.shoopping.mapper.RoleMapper;
import com.shoopping.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色业务实现
 *
 * @author shopping-team
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<Role> getAllEnabledRoles() {
        return lambdaQuery()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getSort)
                .list();
    }
}
