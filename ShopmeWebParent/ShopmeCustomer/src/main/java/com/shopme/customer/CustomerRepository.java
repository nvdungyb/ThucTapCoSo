package com.shopme.customer;

import com.shopme.common.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    Customer getCustomerByEmail(String email);

    Customer findByEmail(String email);
}
