package com.shoopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shoopping.entity.Cart;
import com.shoopping.model.vo.CartVO;
import java.util.List;

public interface CartService extends IService<Cart> {
    List<CartVO> getMyCart(Long userId);
    void addToCart(Long userId, Long productId, Integer quantity);
    void updateQuantity(Long id, Integer quantity, Long userId);
    void updateChecked(Long id, Integer checked, Long userId);
    void removeItem(Long id, Long userId);
    void clearCart(Long userId);
}
