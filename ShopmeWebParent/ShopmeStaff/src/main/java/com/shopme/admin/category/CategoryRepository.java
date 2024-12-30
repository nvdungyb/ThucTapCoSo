package com.shopme.admin.category;

import com.shopme.common.shop.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>, CrudRepository<Category, Integer> {

    @Query("UPDATE Category u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Category getById(Integer id);

    Category findByName(String name);

    Category findByAlias(String alias);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> searchKeyword(String keyword, Pageable pageable);
}
