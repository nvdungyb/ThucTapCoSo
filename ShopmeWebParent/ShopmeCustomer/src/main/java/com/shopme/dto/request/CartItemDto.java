package com.shopme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class CartItemDto {
    @NotNull
    @Min(value = 1, message = "Product ID must not be less than 1")
    private long productId;
    @NotNull
    @Min(value = 1, message = "Quantity must not be less than 1")
    private int quantity;
    private boolean isSelected;
    private Date createdAt;
    // productDiscountId: we will use this field later.

    public CartItemDto() {
        this.isSelected = false;
        this.createdAt = new Date();
    }
}
