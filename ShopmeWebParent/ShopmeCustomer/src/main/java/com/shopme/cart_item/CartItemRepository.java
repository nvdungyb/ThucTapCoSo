package com.shopme.cart_item;

import com.shopme.common.shop.CartItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
    CartItem findByProductId(Integer productId);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.id = :id")
    void updateQuantity(@Param("id") Integer id, @Param("quantity") int quantity);

    List<CartItem> findAllByCartId(Integer id);
}
