package com.shopme.Controller;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.common.dto.ApiResponse;
import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.dto.request.SellerRegisterDto;
import com.shopme.dto.response.SellerResponseDto;
import com.shopme.mapper.BookMapper;
import com.shopme.security.UserDetailsImpl;
import com.shopme.service.ProductService;
import com.shopme.service.SellerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class SellerController {
    private final SellerService sellerService;
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
    private final BookMapper bookMapper;

    public SellerController(SellerService sellerService, ProductService productService, BookMapper bookMapper) {
        this.sellerService = sellerService;
        this.productService = productService;
        this.bookMapper = bookMapper;
    }

    @PostMapping("/seller/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody SellerRegisterDto registerDto) throws EmailAlreadyExistsException, RoleNotFoundException {
        logger.info("DTO: {}", registerDto);

        Seller seller = sellerService.register(registerDto);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New Seller account has been created successfully")
                .data(SellerResponseDto.build(seller))
                .path("/seller/register")
                .build()
        );
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/seller/create/book")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long sellerId = userDetails.getId();

        logger.info("DTO: {}", bookCreateDto);
        Book book = productService.createBook(bookCreateDto, sellerId);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New Book has been created successfully")
                .data(bookMapper.toDto(book))
                .path("/seller/create/book")
                .build()
        );
    }
}
