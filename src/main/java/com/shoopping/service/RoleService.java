package com.shoopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoopping.entity.Role;

import java.util.List;

/**
 * 角色业务接口
 *
 * @author shopping-team
 */
public interface RoleService extends IService<Role> {

    /**
     * 查询所有启用状态的角色（用于角色分配下拉列表）
     */
    List<Role> getAllEnabledRoles();
}
