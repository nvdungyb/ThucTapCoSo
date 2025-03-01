package com.shopme.dto.response;

import com.shopme.common.enums.EDeliveryStatus;
import com.shopme.common.enums.EPaymentMethod;
import com.shopme.common.enums.EPaymentStatus;
import com.shopme.common.enums.EReturnStatus;
import com.shopme.common.shop.ProductDiscount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private long productId;
    private double productPrice;
    private int quantity;
    private EDeliveryStatus deliveryStatus;
    private EReturnStatus returnStatus;
    private Date createAt;
    private double discountPercentage;
}
