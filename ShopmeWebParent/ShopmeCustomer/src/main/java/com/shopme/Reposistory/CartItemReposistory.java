package com.shopme.Reposistory;

import com.shopme.common.shop.CartItem;
import com.shopme.common.shop.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemReposistory extends CrudRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProduct(Long cartId, Product product);
}
