package com.shopme.Reposistory;

import com.shopme.common.shop.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.id = ?1")
    public Page<Product> listByCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    public Product findByAlias(String alias);

    Optional<Product> findById(@Param("id") Long id);

    Optional<Product> findProductByIdAndEnabled(Long id, boolean enabled);

    //    @Query(value = "SELECT * FROM products p WHERE p.enabled = true AND " +
//            "MATCH(p.name, p.alias, p.short_description, p.full_description) " +
//            "AGAINST(:key IN NATURAL LANGUAGE MODE)",
//            nativeQuery = true)

    // todo: if you have large data, you should use the elasticsearch.
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :key, '%')) " +
            "OR LOWER(p.alias) LIKE LOWER(CONCAT('%', :key, '%')) " +
            "OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :key, '%')) " +
            "OR LOWER(p.fullDescription) LIKE LOWER(CONCAT('%', :key, '%')))")
    List<Product> findProductsByKey(String key);

    List<Product> findAllByCategory_Id(Long categoryId);
}
//    OR p.category.parent.name LIKE %?2%"