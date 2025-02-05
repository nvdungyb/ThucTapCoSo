package com.dzungyb.security.reposistory;

import com.shopme.common.entity.Role;
import com.shopme.common.enums.ERole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.eRole = ?1")
    Optional<Role> findByERole(ERole eRole);
}
