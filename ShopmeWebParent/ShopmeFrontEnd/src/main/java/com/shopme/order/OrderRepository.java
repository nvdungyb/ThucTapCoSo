package com.shopme.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    List<Order> findAllByCustomer(Customer customer);
}
