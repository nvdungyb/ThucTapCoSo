package com.shopme.Reposistory;

import com.shopme.common.shop.Laptop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopReposistory extends CrudRepository<Laptop, Long> {
    boolean existsByAlias(String alias);
}
