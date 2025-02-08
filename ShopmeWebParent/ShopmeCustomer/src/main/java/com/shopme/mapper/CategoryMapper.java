package com.shopme.mapper;

import com.shopme.common.shop.Category;
import com.shopme.dto.request.CategoryDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryDto categoryDto, Category parent) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setAlias(categoryDto.getAlias());
        category.setImage(categoryDto.getImage());
        category.setEnabled(categoryDto.isEnabled());
        category.setParent(parent);
        return category;
    }

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .name(category.getName())
                .alias(category.getAlias())
                .image(category.getImage())
                .enabled(category.isEnabled())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
        return categoryDto;
    }
}
