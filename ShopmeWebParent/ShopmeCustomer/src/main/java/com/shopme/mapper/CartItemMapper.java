package com.shopme.mapper;

import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.CartItemDto;
import org.springframework.stereotype.Component;


@Component
public class CartItemMapper {
    public CartItem toEntity(CartItemDto cartItemDto, Cart cart) {
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(new Product(cartItemDto.getProductId()))
                .quantity(cartItemDto.getQuantity())
                .isSelected(cartItemDto.isSelected())
                .createdAt(cartItemDto.getCreatedAt())
                .build();

        return cartItem;
    }
}
