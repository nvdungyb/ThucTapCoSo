package com.shopme.Reposistory;

import com.shopme.common.shop.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryReposistory extends CrudRepository<Category, Long> {
    Optional<Category> findCategoriesByName(String name);

    Optional<Category> findCategoriesByAlias(String alias);

    Optional<Category> findCategoriesByNameOrAlias(String name, String alias);

    List<Category> findAllByParentId(Long id);
}
