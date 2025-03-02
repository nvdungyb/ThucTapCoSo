package com.shopme.Reposistory;

import com.shopme.common.shop.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReposistory extends CrudRepository<Order, Long> {
}
