package com.shopme.admin.shoe;

import com.shopme.common.shop.Shoe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoeRepository extends CrudRepository<Shoe, Integer>, PagingAndSortingRepository<Shoe, Integer> {

    @Query("SELECT p FROM Shoe p WHERE p.name LIKE %?1% OR p.shortDescription LIKE %?1% OR p.fullDescription LIKE %?1%")
    Page<Shoe> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Shoe p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
