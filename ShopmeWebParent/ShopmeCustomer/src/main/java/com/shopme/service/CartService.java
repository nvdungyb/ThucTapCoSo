package com.shopme.service;

import com.shopme.Reposistory.CartItemReposistory;
import com.shopme.Reposistory.CartReposistory;
import com.shopme.Reposistory.ProductRepository;
import com.shopme.common.entity.User;
import com.shopme.common.shop.Cart;
import com.shopme.common.shop.CartItem;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.CartItemDto;
import com.shopme.mapper.CartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class CartService {
    private final CartReposistory cartRepository;
    private final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final CartItemMapper cartItemMapper;
    private final ProductRepository productRepository;
    private final CartItemReposistory cartItemReposistory;
    private final CartReposistory cartReposistory;

    public CartService(CartReposistory cartRepository, CartItemMapper cartItemMapper, ProductRepository productRepository, CartItemReposistory cartItemReposistory, CartReposistory cartReposistory) {
        this.cartRepository = cartRepository;
        this.cartItemMapper = cartItemMapper;
        this.productRepository = productRepository;
        this.cartItemReposistory = cartItemReposistory;
        this.cartReposistory = cartReposistory;
    }

    public Cart getCartOrCreateNew(Long userId) {
        Cart cart = cartRepository.findByUser_Id((userId))
                .orElseGet(() -> cartReposistory.save(Cart.builder()
                        .createAt(new Date())
                        .isActive(true)
                        .user(User.builder().id(userId).build())
                        .updateAt(new Date())
                        .build()));

        return cart;
    }

    public Cart addProductToCart(CartItemDto cartItemDto, Long userId) {
        // 1. Find product by id and check if it's enabled
        Product product = productRepository.findProductByIdAndEnabled(cartItemDto.getProductId(), true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // 2. Get cart or create a new one
        Cart userCart = getCartOrCreateNew(userId);

        // 3. Check if product is already in cart
        Optional<CartItem> existingCartItem = cartItemReposistory.findByCart_IdAndProduct(userCart.getId(), product);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + cartItemDto.getQuantity();

            // 4. Validate stock before updating quantity
            if (newQuantity > product.getStockQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product quantity exceeds available stock");
            }

            cartItem.setQuantity(newQuantity);
        } else {
            // 5. Create a new cart item
            if (cartItemDto.getQuantity() > product.getStockQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product quantity exceeds available stock");
            }

            cartItem = cartItemMapper.toEntity(cartItemDto, userCart);
            userCart.getCartItems().add(cartItem);  // Ensure it's added only once
        }

        // 6. Save updated cart
        return cartRepository.save(userCart);
    }
}
