package com.shopme.Reposistory;

import com.shopme.common.entity.User;
import com.shopme.common.shop.Cart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartReposistory extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);

    Long user(User user);

    Optional<Cart> findByUser(User user);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id in ?1")
    void deleteCartItems(List<Long> cartItemIds);
}
