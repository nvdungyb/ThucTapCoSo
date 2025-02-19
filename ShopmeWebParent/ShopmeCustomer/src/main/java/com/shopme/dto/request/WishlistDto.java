package com.shopme.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class WishlistDto {
    @NotNull(message = "Product ID cannot be null")
    @Min(value = 1, message = "Product id must be greater than 0")
    private long productId;
}
