package com.shopme.admin.seller;

import com.shopme.common.entity.Seller;
import jakarta.persistence.Inheritance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface SellerRepository extends CrudRepository<Seller, Integer>, PagingAndSortingRepository<Seller, Integer> {
    @Query("SELECT u FROM Seller u WHERE u.email = :email")
    public Seller getUserByEmail(@Param("email") String email);

    public long countById(Integer id);

    @Query("UPDATE Seller u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT u FROM Seller u WHERE CONCAT(u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<Seller> findAll(String keyword, Pageable pageable);
}


/*
   +) Query: This annotation indicate that the following code snippet is a custom JPQL
   JPQL allows we to write database queries using java syntax.

   +) @Param("email") String email : this part defines a method parameter named email of type String.
   The @Param annotation is used to map this parameter to the :email placeholder in the JPQL query.
 */