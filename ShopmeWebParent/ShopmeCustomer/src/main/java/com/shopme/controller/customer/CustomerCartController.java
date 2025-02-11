package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.dto.request.CartItemDto;
import com.shopme.dto.request.CartItemUpdateDto;
import com.shopme.mapper.CartItemMapper;
import com.shopme.mapper.CartMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerCartController {
    private final CartService cartService;
    private final Logger logger = LoggerFactory.getLogger(CustomerCartController.class);
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    public CustomerCartController(CartService cartService, CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @GetMapping("/customers/cart")
    public ResponseEntity<?> viewCart(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("View cart for user with id: " + userId);

        Cart cart = cartService.getCartOrCreateNew(userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Cart has been retrieved successfully")
                .data(cartMapper.toDto(cart))
                .path(request.getRequestURI())
                .build());
    }

    @PostMapping("/customers/cart/add")
    public ResponseEntity<?> addProductToCart(@Valid @RequestBody CartItemDto cartItemDto, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("Add product to cart for user with id: " + userId);

        Cart cart = cartService.addProductToCart(cartItemDto, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Product has been added to cart successfully")
                .data(cartMapper.toDto(cart))
                .path(request.getRequestURI())
                .build());
    }

    @PutMapping("/customers/cart/update")
    public ResponseEntity<?> updateCartItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDto, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("Update cart item for user with id: " + userId);

        CartItem cartItem = cartService.updateCartItem(cartItemUpdateDto, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Cart item has been updated successfully")
                .data(cartItemMapper.toDto(cartItem))
                .path(request.getRequestURI())
                .build());
    }

    @DeleteMapping("/customers/cart/remove/{id}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable("id") Long cartItemId, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("Remove product from cart for user with id: " + userId);

        cartService.removeProductFromCart(cartItemId, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Product has been removed from cart successfully")
                .path(request.getRequestURI())
                .build());
    }

    @PutMapping("/customers/cart/select/{id}")
    public ResponseEntity<?> selectCartItem(@PathVariable("id") Long cartItemId,
                                            @RequestParam(name = "selected", defaultValue = "true") boolean selected,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();
        logger.info("Select cart item id {} for user with id: {}", cartItemId, userId);

        Integer isSelected = cartService.selectCartItem(cartItemId, userId, selected);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Cart item has been selected successfully")
                .data(isSelected)
                .path(request.getRequestURI())
                .build());
    }
}
