package com.shopme.Reposistory;

import com.shopme.common.shop.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartReposistory extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);
}
