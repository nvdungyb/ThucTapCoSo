package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>, CrudRepository<Category, Integer> {

    @Query("UPDATE Category u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

}
