package com.shoopping.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoopping.entity.Cart;
import com.shoopping.entity.Product;
import com.shoopping.mapper.CartMapper;
import com.shoopping.mapper.ProductMapper;
import com.shoopping.model.vo.CartVO;
import com.shoopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final ProductMapper productMapper;

    @Override
    public List<CartVO> getMyCart(Long userId) {
        List<Cart> items = lambdaQuery().eq(Cart::getUserId, userId).orderByDesc(Cart::getCreateTime).list();
        if (items.isEmpty()) return Collections.emptyList();
        Map<Long, Product> productMap = productMapper.selectBatchIds(
            items.stream().map(Cart::getProductId).collect(Collectors.toSet()))
            .stream().collect(Collectors.toMap(Product::getId, p -> p));
        return items.stream().map(item -> {
            CartVO vo = new CartVO();
            BeanUtils.copyProperties(item, vo);
            Product p = productMap.get(item.getProductId());
            if (p != null) {
                vo.setProductName(p.getName());
                vo.setPrice(p.getPrice());
                vo.setMainImage(p.getMainImage());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        Cart exist = lambdaQuery().eq(Cart::getUserId, userId).eq(Cart::getProductId, productId).one();
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + (quantity != null ? quantity : 1));
            updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity != null ? quantity : 1);
            cart.setChecked(1);
            save(cart);
        }
    }

    @Override
    public void updateQuantity(Long id, Integer quantity, Long userId) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) throw new IllegalArgumentException("购物车项不存在");
        cart.setQuantity(quantity);
        updateById(cart);
    }

    @Override
    public void updateChecked(Long id, Integer checked, Long userId) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) throw new IllegalArgumentException("购物车项不存在");
        cart.setChecked(checked);
        updateById(cart);
    }

    @Override
    public void removeItem(Long id, Long userId) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) throw new IllegalArgumentException("购物车项不存在");
        removeById(id);
    }

    @Override
    public void clearCart(Long userId) {
        lambdaUpdate().eq(Cart::getUserId, userId).remove();
    }
}
