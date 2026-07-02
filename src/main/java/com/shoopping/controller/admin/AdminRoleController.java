package com.shoopping.controller.admin;

import com.shoopping.common.Result;
import com.shoopping.entity.Role;
import com.shoopping.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器（管理员）
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping
    public Result<List<Role>> list() {
        return Result.success(roleService.getAllEnabledRoles());
    }
}
