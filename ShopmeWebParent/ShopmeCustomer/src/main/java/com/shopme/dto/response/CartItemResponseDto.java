package com.shopme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private long cartId;
    private long productId;
    private int quantity;
    private boolean isSelected;
    private Date createdAt;
}
