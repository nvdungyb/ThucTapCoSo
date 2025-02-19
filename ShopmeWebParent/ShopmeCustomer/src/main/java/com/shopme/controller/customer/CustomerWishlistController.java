package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.WishlistItem;
import com.shopme.dto.request.WishlistDto;
import com.shopme.mapper.WishlistMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

@Slf4j
@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerWishlistController {
    private final WishlistService wishlistService;
    private final WishlistMapper wishlistMapper;
    private final Logger logger = LoggerFactory.getLogger(CustomerWishlistController.class);

    public CustomerWishlistController(WishlistService wishlistService, WishlistMapper wishlistMapper) {
        this.wishlistService = wishlistService;
        this.wishlistMapper = wishlistMapper;
    }

    @GetMapping("/customers/wishlist")
    public ResponseEntity<?> viewWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        long userId = userDetails.getId();
        logger.info("View wishlist for user with id: {}", userId);

        List<WishlistItem> wishList = wishlistService.getWishlist(userId);
        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(200)
                        .message("Wishlist has been retrieved successfully")
                        .data(wishlistMapper.toDto(wishList))
                        .path(request.getRequestURI())
                        .build());
    }

    @PostMapping("/customers/wishlist/toggle")
    public ResponseEntity<?> addProductToWishlist(@Valid @RequestBody WishlistDto wishlistDto, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        long userId = userDetails.getId();
        logger.info("Add product to wishlist for user with id: {}", userId);

        wishlistService.toggleProductToWishlist(wishlistDto, userId);
        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(200)
                        .message("This operation has been completed successfully")
                        .path(request.getRequestURI())
                        .build());
    }
}
