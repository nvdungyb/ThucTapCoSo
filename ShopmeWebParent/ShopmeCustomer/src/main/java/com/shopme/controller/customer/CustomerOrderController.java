package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Order;
import com.shopme.mapper.OrderMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerOrderController {
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);
    private final OrderMapper orderMapper;

    public CustomerOrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping("/orders/create")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("Create order for user with id: " + userId);

        Order order = orderService.createOrder(userId);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(HttpStatus.OK.value())
                        .path(request.getRequestURI())
                        .data(orderMapper.toDto(order))
                        .message("Order created successfully!")
                        .build());
    }

    /**
     * POST /order/create – Đặt hàng
     * GET /order/{id} – Xem chi tiết đơn hàng
     * GET /orders – Xem lịch sử đơn hàng
     * PUT /order/cancel/{id} – Hủy đơn hàng
     */
}
