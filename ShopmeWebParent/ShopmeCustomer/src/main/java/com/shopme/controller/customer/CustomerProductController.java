package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Category;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.CategoryDto;
import com.shopme.dto.response.ProductResponseDto;
import com.shopme.mapper.CategoryMapper;
import com.shopme.mapper.ProductMapper;
import com.shopme.service.CategoryService;
import com.shopme.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class CustomerProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(CustomerProductController.class);
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    public CustomerProductController(ProductService productService, ProductMapper productMapper, CategoryMapper categoryMapper, CategoryService categoryService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getDetailProductForUser(@PathVariable("id") Long id) {
        logger.info("Get detail product for user with id: " + id);

        Product product = productService.getDetailProductForCustomer(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get detail product for user successfully")
                .data(productMapper.toProductResponseDto(product))
                .path("/products/{id}")
                .build());
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        logger.info("Get all categories");

        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> responseCategories = categories.stream()
                .map(categoryMapper::toDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get all categories successfully")
                .data(responseCategories)
                .path("/categories")
                .build());
    }

    @GetMapping("/categories/{id}/subcategories")
    public ResponseEntity<?> getAllSubCategories(@PathVariable("id") Long id) {
        logger.info("Get all sub categories with parent id: " + id);

        List<Category> categories = categoryService.getAllSubCategories(id);
        List<CategoryDto> responseCategories = categories.stream()
                .map(categoryMapper::toDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get all sub categories successfully")
                .data(responseCategories)
                .path("/categories/" + id + "/subcategories")
                .build());
    }

    @GetMapping("/categories/{id}/products")
    public ResponseEntity<?> getProductsByCategory(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.info("Get all products by category id: " + id);

        List<Product> products = productService.getProductsByCategory(id);
        List<ProductResponseDto> responseProducts = products.stream()
                .map(productMapper::toProductResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get all products by category id successfully")
                .data(responseProducts)
                .path(request.getRequestURI())
                .build());
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(@RequestParam("key") String key, HttpServletRequest request) {
        logger.info("Search products with keyword: " + key);

        List<Product> products = productService.searchProductsByKey(key);
        List<ProductResponseDto> responseProducts = products.stream()
                .map(productMapper::toProductResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Search products successfully")
                .data(responseProducts)
                .path(request.getRequestURI())
                .build());
    }

    /**
     * GET /products/filter?category={id}&price_min={min}&price_max={max} – Lọc
     */

    @GetMapping("/products/filter")
    public ResponseEntity<?> filterProducts(@RequestParam(value = "categoryId") Long categoryId,
                                            @RequestParam(value = "price_min", required = false) Double minPrice,
                                            @RequestParam(value = "price_max", required = false) Double maxPrice,
                                            HttpServletRequest request) {
        logger.info("Filter products with category: " + categoryId + ", min price: " + minPrice + ", max price: " + maxPrice);

        List<Product> products = productService.filterProducts(categoryId, minPrice, maxPrice);
        List<ProductResponseDto> responseProducts = products.stream()
                .map(productMapper::toProductResponseDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Filter products successfully")
                .data(responseProducts)
                .path(request.getRequestURI())
                .build());
    }
}
