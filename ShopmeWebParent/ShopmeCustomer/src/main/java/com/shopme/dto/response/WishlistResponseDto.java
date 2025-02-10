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
public class WishlistResponseDto {
    private long wishlistId;
    private long userId;
    private long productId;
    private String productName;
    private String productAlias;
    private String productShortDescription;
    private Date createdAt;
}
