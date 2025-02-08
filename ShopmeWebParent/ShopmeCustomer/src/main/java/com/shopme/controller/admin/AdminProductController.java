package com.shopme.controller.admin;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Category;
import com.shopme.dto.request.CategoryDto;
import com.shopme.mapper.CategoryMapper;
import com.shopme.service.CategoryService;
import com.shopme.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminProductController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final Logger logger = LoggerFactory.getLogger(AdminProductController.class);
    private final CategoryMapper categoryMapper;

    public AdminProductController(CategoryService categoryService, ProductService productService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping("/admin/categories/add")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        logger.info("Create category: {}", categoryDto);

        Category category = categoryService.createCategory(categoryDto);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Create category successfully")
                .data(categoryMapper.toDto(category))
                .build());
    }
}
