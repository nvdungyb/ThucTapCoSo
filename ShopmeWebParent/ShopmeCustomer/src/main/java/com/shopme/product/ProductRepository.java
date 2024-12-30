package com.shopme.product;

import com.shopme.common.shop.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.id = ?1")
    public Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    public Product findByAlias(String alias);
}
//    OR p.category.parent.name LIKE %?2%"