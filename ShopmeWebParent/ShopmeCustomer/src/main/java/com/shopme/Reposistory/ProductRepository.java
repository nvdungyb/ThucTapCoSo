package com.shopme.Reposistory;

import com.shopme.common.shop.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.id = ?1")
    public Page<Product> listByCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    public Product findByAlias(String alias);

    Optional<Product> findProductById(@Param("id") Long id);

    Optional<Product> findProductByIdAndEnabled(Long id, boolean enabled);
}
//    OR p.category.parent.name LIKE %?2%"