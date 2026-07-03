package com.shoopping.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shoopping.common.Result;
import com.shoopping.entity.UserRole;
import com.shoopping.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDataController {

    private final UserRoleMapper userRoleMapper;

    @GetMapping("/user-role")
    public Result<Page<UserRole>> userRoleList(int pageNum, int pageSize) {
        Page<UserRole> page = new Page<>(pageNum, pageSize);
        userRoleMapper.selectPage(page, new LambdaQueryWrapper<UserRole>().orderByDesc(UserRole::getId));
        return Result.success(page);
    }
}
