package com.shopme.Reposistory;

import com.shopme.common.entity.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerReposistory extends CrudRepository<Seller, Long> {
}
