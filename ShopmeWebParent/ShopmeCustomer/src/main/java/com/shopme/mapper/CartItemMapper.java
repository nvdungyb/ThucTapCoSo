package com.shopme.mapper;

import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.CartItemDto;
import com.shopme.dto.response.CartItemResponseDto;
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

    public CartItemResponseDto toDto(CartItem cartItem) {
        CartItemResponseDto cartItemResponseDto = CartItemResponseDto.builder()
                .cartId(cartItem.getCart().getId())
                .productId(cartItem.getProduct().getId())
                .quantity(cartItem.getQuantity())
                .isSelected(cartItem.isSelected())
                .createdAt(cartItem.getCreatedAt())
                .build();

        return cartItemResponseDto;
    }
}
