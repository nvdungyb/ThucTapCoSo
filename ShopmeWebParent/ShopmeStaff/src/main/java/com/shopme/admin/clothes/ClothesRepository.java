package com.shopme.admin.clothes;

import com.shopme.common.shop.Clothes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothesRepository extends CrudRepository<Clothes, Integer>, PagingAndSortingRepository<Clothes, Integer> {

    @Query("SELECT p FROM Clothes p WHERE p.name LIKE %?1% OR p.shortDescription LIKE %?1% OR p.fullDescription LIKE %?1%")
    Page<Clothes> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Clothes p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
