package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Cart;
import com.shopme.dto.request.CartItemDto;
import com.shopme.mapper.CartMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.CartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerCartController {
    private final CartService cartService;
    private final Logger logger = LoggerFactory.getLogger(CustomerCartController.class);
    private final CartMapper cartMapper;

    public CustomerCartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @GetMapping("/customers/cart")
    public ResponseEntity<?> viewCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        logger.info("View cart for user with id: " + userId);

        Cart cart = cartService.getCartOrCreateNew(userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Cart has been retrieved successfully")
                .data(cart)
                .build());
    }

    @PostMapping("/customers/cart/add")
    public ResponseEntity<?> addProductToCart(@Valid @RequestBody CartItemDto cartItemDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        logger.info("Add product to cart for user with id: " + userId);

        Cart cart = cartService.addProductToCart(cartItemDto, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Product has been added to cart successfully")
                .data(cartMapper.toDto(cart))
                .build());
    }

    /**
     * GET /cart – Xem giỏ hàng
     * PUT /cart/update/{id} – Cập nhật số lượng sản phẩm trong giỏ hàng
     * DELETE /cart/remove/{id} – Xóa sản phẩm khỏi giỏ hàng
     *
     */
}
