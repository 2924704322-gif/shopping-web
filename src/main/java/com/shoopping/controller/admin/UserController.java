package com.shoopping.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shoopping.common.Result;
import com.shoopping.model.user.UserCreateRequest;
import com.shoopping.model.user.UserQueryParam;
import com.shoopping.model.user.UserUpdateRequest;
import com.shoopping.model.user.UserVO;
import com.shoopping.security.UserDetailsImpl;
import com.shoopping.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器（管理员）
 *
 * @author shopping-team
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    /** 分页列表 */
    @GetMapping
    public Result<Page<UserVO>> list(UserQueryParam param) {
        return Result.success(userService.getUserPage(param));
    }

    /** 用户详情 */
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /** 创建用户 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success("创建成功", userService.createUser(request));
    }

    /** 更新用户 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success("更新成功", null);
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id, getCurrentUserId());
        return Result.success("删除成功", null);
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/status")
    public Result<Void> setStatus(@PathVariable Long id,
                                  @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error(400, "状态值无效，必须为 0 或 1");
        }
        userService.setUserStatus(id, status, getCurrentUserId());
        return Result.success("操作成功", null);
    }

    /** 分配角色 */
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id,
                                    @RequestBody @NotEmpty(message = "角色列表不能为空") List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success("角色分配成功", null);
    }

    /** 获取当前登录用户ID */
    private Long getCurrentUserId() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();
    }
}
