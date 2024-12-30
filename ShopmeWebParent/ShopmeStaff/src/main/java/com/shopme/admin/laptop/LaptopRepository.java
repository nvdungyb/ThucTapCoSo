package com.shopme.admin.laptop;

import com.shopme.common.shop.Laptop;
import com.shopme.common.shop.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopRepository extends CrudRepository<Laptop, Integer>, PagingAndSortingRepository<Laptop, Integer> {

    @Query("SELECT p FROM Laptop p WHERE p.name LIKE %?1% OR p.shortDescription LIKE %?1% OR p.fullDescription LIKE %?1%")
    Page<Laptop> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Laptop p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
