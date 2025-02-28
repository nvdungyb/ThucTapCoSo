package com.shopme.Reposistory;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressReposistory extends CrudRepository<Address, Long> {
    Address user(User user);
}
