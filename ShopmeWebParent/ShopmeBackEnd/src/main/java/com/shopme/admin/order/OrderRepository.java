package com.shopme.admin.order;

import com.shopme.common.entity.Order;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer>, PagingAndSortingRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE CONCAT(o.customer.firstName, ' ', o.customer.lastName) LIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Order o SET o.orderStatus = ?2 WHERE o.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
