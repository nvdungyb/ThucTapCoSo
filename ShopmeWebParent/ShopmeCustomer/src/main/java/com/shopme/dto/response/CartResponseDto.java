package com.shopme.dto.response;

import com.shopme.dto.request.CartItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private long cartId;
    private long userId;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private List<CartItemDto> cartItemDtos;
}
