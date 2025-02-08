package com.shopme.controller.customer;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Product;
import com.shopme.dto.response.ProductResponseDto;
import com.shopme.mapper.ProductMapper;
import com.shopme.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class CustomerProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(CustomerProductController.class);

    public CustomerProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
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
}
