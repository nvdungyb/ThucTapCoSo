package com.shopme.Reposistory;

import com.shopme.common.entity.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerReposistory extends CrudRepository<Seller, Long> {
    Optional<Seller> findById(Long aLong);

    Optional<Seller> findByUserId(Long sellerId);
}
