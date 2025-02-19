package com.shopme.service;

import com.shopme.Reposistory.ProductRepository;
import com.shopme.Reposistory.WishlistItemReposistory;
import com.shopme.common.entity.User;
import com.shopme.common.shop.Product;
import com.shopme.common.shop.WishlistItem;
import com.shopme.dto.request.WishlistDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class WishlistService {
    private final WishlistItemReposistory wishlistItemReposistory;
    private final ProductRepository productRepository;
    private Logger logger = LoggerFactory.getLogger(WishlistService.class);

    public WishlistService(WishlistItemReposistory wishlistItemReposistory, ProductRepository productRepository) {
        this.wishlistItemReposistory = wishlistItemReposistory;
        this.productRepository = productRepository;
    }

    // todo: need to support pagination
    public List<WishlistItem> getWishlist(long userId) {
        return wishlistItemReposistory.findAllByUser_Id(userId);
    }

    @Transactional
    public void toggleProductToWishlist(@Valid WishlistDto wishlistDto, long userId) {
        logger.info("Adding product id {} to wishlist for user with id: {}", wishlistDto.getProductId(), userId);
        Product product = productRepository.findProductByIdAndEnabled(wishlistDto.getProductId(), true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (wishlistItemReposistory.existsByUser_IdAndProduct_Id(userId, wishlistDto.getProductId())) {
            wishlistItemReposistory.deleteByUser_IdAndProduct_Id(userId, wishlistDto.getProductId());
            logger.info("Removed product {} from wishlist for user {}", wishlistDto.getProductId(), userId);
        } else {
            // todo: tìm hiểu lỗi detached là gì? tại sao user detached được nhưng product thì không? (CascadeType)
            WishlistItem wishlistItem = WishlistItem.builder()
                    .user(new User(userId))
                    .product(product)
                    .createdAt(new Date())
                    .build();
            wishlistItemReposistory.save(wishlistItem);
            logger.info("Added product {} to wishlist for user {}", wishlistDto.getProductId(), userId);
        }
    }
}
