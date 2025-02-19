package com.shopme.Reposistory;

import com.shopme.common.shop.WishlistItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistReposistory extends CrudRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByUser_Id(Long userId);

    Optional<WishlistItem> findByUser_IdAndProduct_Id(Long userId, Long productId);
}
