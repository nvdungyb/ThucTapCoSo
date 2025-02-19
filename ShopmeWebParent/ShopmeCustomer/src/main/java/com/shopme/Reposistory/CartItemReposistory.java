package com.shopme.Reposistory;

import com.shopme.common.shop.CartItem;
import com.shopme.common.shop.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemReposistory extends CrudRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProduct(Long cartId, Product product);

    Optional<CartItem> findByIdAndCart_User_Id(Long id, Long cartUserId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.cart c JOIN FETCh c.user WHERE ci.id = :cartItemId")
    Optional<CartItem> findCartItemWithUser(Long cartItemId);

    @Transactional
    @Modifying
    @Query("Update CartItem ci SET ci.isSelected = :selected WHERE ci.id = :cartItemId AND ci.cart.user.id = :userId")
    Integer updateSelected(Long cartItemId, Long userId, boolean selected);
}
