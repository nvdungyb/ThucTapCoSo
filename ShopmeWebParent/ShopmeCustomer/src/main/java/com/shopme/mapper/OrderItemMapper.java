package com.shopme.mapper;

import com.shopme.common.shop.OrderItem;
import com.shopme.dto.response.OrderItemResponseDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItemResponseDto toDto(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .productId(orderItem.getProduct().getId())
                .productPrice(orderItem.getProductPrice())
                .quantity(orderItem.getQuantity())
                .deliveryStatus(orderItem.getDeliveryStatus())
                .returnStatus(orderItem.getReturnStatus())
                .createAt(orderItem.getCreateAt())
                .discountPercentage(orderItem.getDiscount() == null ? 0.0 : orderItem.getDiscount().getDiscountPercentage())
                .build();
    }
}
