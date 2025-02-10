package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.WishlistItem;
import com.shopme.mapper.WishlistMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<?> viewWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        long userId = userDetails.getId();
        logger.info("View wishlist for user with id: {}", userId);

        List<WishlistItem> wishList = wishlistService.getWishlist(userId);
        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(200)
                        .message("Wishlist has been retrieved successfully")
                        .data(wishlistMapper.toDto(wishList))
                        .build());
    }

    /**
     * GET /wishlist – Xem danh sách yêu thích
     * POST /wishlist/add – Thêm sản phẩm vào danh sách yêu thích
     * DELETE /wishlist/remove/{id} – Xóa sản phẩm khỏi danh sách yêu thích
     */
}
