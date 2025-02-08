package com.shopme.service;

import com.shopme.Reposistory.CategoryReposistory;
import com.shopme.common.shop.Category;
import com.shopme.dto.request.CategoryDto;
import com.shopme.mapper.CategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryReposistory categoryReposistory;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryReposistory categoryReposistory, CategoryMapper categoryMapper) {
        this.categoryReposistory = categoryReposistory;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        categoryReposistory.findCategoriesByNameOrAlias(categoryDto.getName(), categoryDto.getAlias())
                .ifPresent((category) -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name '" + categoryDto.getName() + "' or alias '" + categoryDto.getAlias() + "' already exists");
                });

        Category parentCategory = (categoryDto.getParentId() != null) ?
                categoryReposistory.findById(categoryDto.getParentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found"))
                : null;

        Category category = categoryMapper.toEntity(categoryDto, parentCategory);
        return categoryReposistory.save(category);
    }

    public List<Category> getAllCategories() {
        return (List<Category>) categoryReposistory.findAll();
    }

    public List<Category> getAllSubCategories(Long id) {
        return categoryReposistory.findAllByParentId(id);
    }
}
