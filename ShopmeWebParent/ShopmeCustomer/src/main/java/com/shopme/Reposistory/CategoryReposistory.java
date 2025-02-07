package com.shopme.Reposistory;

import com.shopme.common.shop.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryReposistory extends CrudRepository<Category, Long> {
}
