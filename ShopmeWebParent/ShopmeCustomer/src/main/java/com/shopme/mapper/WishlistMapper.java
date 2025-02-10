package com.shopme.mapper;

import com.shopme.common.shop.WishlistItem;
import com.shopme.dto.response.WishlistResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WishlistMapper {
    public List<WishlistResponseDto> toDto(List<WishlistItem> wishList) {
        List<WishlistResponseDto> wishlistResponseDtos = wishList.isEmpty() ?
                wishList.stream()
                        .map(wishlistItem -> WishlistResponseDto.builder()
                                .wishlistId(wishlistItem.getId())
                                .userId(wishlistItem.getUser().getId())
                                .productId(wishlistItem.getProduct().getId())
                                .productName(wishlistItem.getProduct().getName())
                                .productAlias(wishlistItem.getProduct().getAlias())
                                .productShortDescription(wishlistItem.getProduct().getShortDescription())
                                .createdAt(wishlistItem.getCreatedAt())
                                .build()
                        ).toList()
                : List.of();

        return wishlistResponseDtos;
    }
}
