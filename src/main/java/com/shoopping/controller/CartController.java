package com.shoopping.controller;

import com.shoopping.common.Result;
import com.shoopping.model.vo.CartVO;
import com.shoopping.security.UserDetailsImpl;
import com.shoopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Long getUserId() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    @GetMapping
    public Result<List<CartVO>> list() {
        return Result.success(cartService.getMyCart(getUserId()));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        Long productId = ((Number) body.get("productId")).longValue();
        Integer quantity = body.get("quantity") != null ? ((Number) body.get("quantity")).intValue() : 1;
        cartService.addToCart(getUserId(), productId, quantity);
        return Result.success("已加入购物车", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        cartService.updateQuantity(id, body.get("quantity"), getUserId());
        return Result.success(null);
    }

    @PutMapping("/{id}/check")
    public Result<Void> check(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        cartService.updateChecked(id, body.get("checked"), getUserId());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.removeItem(id, getUserId());
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clear() {
        cartService.clearCart(getUserId());
        return Result.success(null);
    }
}
