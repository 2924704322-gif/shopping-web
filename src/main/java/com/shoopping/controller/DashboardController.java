package com.shoopping.controller;

import com.shoopping.common.Result;
import com.shoopping.entity.Product;
import com.shoopping.service.ProductService;
import com.shoopping.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 仪表盘公开统计接口（所有登录用户可访问）
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        long onShelf = productService.lambdaQuery().eq(Product::getStatus, 1).count();
        return Result.success(Map.of(
                "userCount", userService.count(),
                "productCount", productService.count(),
                "onShelfCount", onShelf));
    }
}
