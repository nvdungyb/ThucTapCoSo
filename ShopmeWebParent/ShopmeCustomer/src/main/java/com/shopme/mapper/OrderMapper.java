package com.shopme.mapper;

import com.shopme.common.shop.Order;
import com.shopme.dto.response.OrderResponseDto;
import com.shopme.service.OrderService;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Builder
@Data
@Component
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    public OrderResponseDto toDto(Order order) {
        double discountAmount = orderService.discountAmount(order);
        double totalCost = orderService.totalCost(order);

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .orderTime(order.getOrderTime())
                .shippingAddress(order.getShippingAddress())
                .shippingCost(order.getShippingCost())
                .productPrices(order.getProductPrices())
                .discountAmount(discountAmount)
                .totalCost(totalCost)
                .orderStatus(order.getOrderStatus())
                .userId(order.getUser().getId())
                .updateDate(order.getUpdateDate())
                .payments(order.getPayments().stream().map(paymentMapper::toDto).toList())
                .couponOrderId(order.getCouponOrder() == null ? null : order.getCouponOrder().getId())
                .orderItemList(order.getOrderItems().stream().map(orderItemMapper::toDto).toList())
                .build();
    }
}
