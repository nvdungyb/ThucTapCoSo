package com.shopme.admin.product;

import com.shopme.common.shop.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE " +
            "(p.name LIKE %:keyword% OR p.shortDescription LIKE %:keyword% OR p.fullDescription LIKE %:keyword%) " +
            "AND p.seller.id = :sellerId")
    Page<Product> findAll(@Param("keyword") String keyword, Pageable pageable, @Param("sellerId") Integer sellerId);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId")
    Page<Product> findAll(Pageable pageable, @Param("sellerId") Integer sellerId);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
