package com.shopme.mapper;

import com.shopme.common.shop.Cart;
import com.shopme.dto.request.CartItemDto;
import com.shopme.dto.response.CartResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {
    public CartResponseDto toDto(Cart cart) {
        CartResponseDto cartResponseDto = CartResponseDto.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .isActive(cart.isActive())
                .createdAt(cart.getCreateAt())
                .updatedAt(cart.getUpdateAt())
                .cartItemDtos(cart.getCartItems().stream()
                        .map(cartItem -> CartItemDto.builder()
                                .productId(cartItem.getProduct().getId())
                                .quantity(cartItem.getQuantity())
                                .isSelected(cartItem.isSelected())
                                .createdAt(cartItem.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return cartResponseDto;
    }
}
