package com.shopme.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.User;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);
}


/*
   +) Query: This annotation indicate that the following code snippet is a custom JPQL
   JPQL allows we to write database queries using java syntax.

   +) @Param("email") String email : this part defines a method parameter named email of type String.
   The @Param annotation is used to map this parameter to the :email placeholder in the JPQL query.
 */