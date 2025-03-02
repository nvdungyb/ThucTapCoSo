package com.shopme.dto.response;

import com.shopme.common.enums.EOrderStatus;
import com.shopme.common.enums.EPaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private long orderId;
    private Date orderTime;
    private float shippingCost;
    private double productPrices;
    private double discountAmount;
    private double totalCost;
    private EOrderStatus orderStatus;
    private String shippingAddress;
    private long userId;
    private Date updateDate;
    //    private Date estimatedDeliveryDate;
//    private String note;
    private List<PaymentResponseDto> payments;
    private Long couponOrderId;
    private List<OrderItemResponseDto> orderItemList;
}
