package com.shopme.Controller;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Product;
import com.shopme.dto.response.ProductResponseDto;
import com.shopme.mapper.ProductMapper;
import com.shopme.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Slf4j
@RestController
public class ProductController {
    private final ProductService productService;
    private final Logger logger = Logger.getLogger(ProductController.class.getName());

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/detail/{id}")
    public ResponseEntity<?> getDetailProductForUser(@PathVariable("id") Long id) {
        logger.info("Get detail product for user with id: " + id);

        Product product = productService.getDetailProductForCustomer(id);

        ProductResponseDto responseDto = ProductMapper.toProductResponseDto(product);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get detail product for user successfully")
                .data(responseDto)
                .build());
    }
}
