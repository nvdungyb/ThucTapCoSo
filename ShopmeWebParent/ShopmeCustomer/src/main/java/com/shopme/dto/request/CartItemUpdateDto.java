package com.shopme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateDto {
    @NotNull(message = "Cart item ID cannot be null")
    private long cartItemId;
    @Min(value = 1, message = "Quantity must not be less than 1")
    private int quantity;
}
