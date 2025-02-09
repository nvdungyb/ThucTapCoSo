package com.shopme.controller.seller;

import com.shopme.common.dto.ApiResponse;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.dto.request.BookUpdateDto;
import com.shopme.mapper.ProductMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('SELLER')")
public class SellerProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(SellerProductController.class);

    public SellerProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping("/seller/products/book/add")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookCreateDto bookCreateDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();

        logger.info("DTO: {}", bookCreateDto);
        Book book = productService.createBook(bookCreateDto, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New Book has been created successfully")
                .data(productMapper.toProductResponseDto(book))
                .path("/seller/products/book/add")
                .build()
        );
    }

    @PutMapping("/seller/products/book/update")
    public ResponseEntity<?> updateBook(@Valid @RequestBody BookUpdateDto bookUpdateDto, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        Long userId = userDetails.getId();

        logger.info("DTO: {}", bookUpdateDto);
        Book book = productService.updateBook(bookUpdateDto, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Book has been updated successfully")
                .data(productMapper.toProductResponseDto(book))
                .path(request.getRequestURI())
                .build()
        );
    }

    @GetMapping("/seller/products/{id}")
    public ResponseEntity<?> getDetailProductForSeller(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();

        logger.info("Get detail product for seller with id: " + id);
        Product product = productService.getDetailProductForSeller(id, userId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Get detail product for seller successfully")
                .data(productMapper.toProductResponseDto(product))
                .path("/seller/products/" + id)
                .build());
    }

    /**
     * POST /seller/products/add – Thêm sản phẩm mới
     * PUT /seller/products/update/{id} – Cập nhật sản phẩm
     * DELETE /seller/products/delete/{id} – Xóa sản phẩm
     * GET /seller/products – Danh sách sản phẩm của người bán
     */
}
