package com.shopme.order;

import com.shopme.common.shop.ProductOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductOrderRepository extends CrudRepository<ProductOrder, Integer> {
    List<ProductOrder> findAllByOrderId(Integer id);
}
