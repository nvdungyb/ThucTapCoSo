package com.shopme.controller.seller;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.common.dto.ApiResponse;
import com.shopme.common.entity.Seller;
import com.shopme.dto.request.SellerRegisterDto;
import com.shopme.dto.response.SellerResponseDto;
import com.shopme.mapper.BookMapper;
import com.shopme.service.ProductService;
import com.shopme.service.SellerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody SellerRegisterDto registerDto, HttpServletRequest request) throws EmailAlreadyExistsException {
        logger.info("DTO: {}", registerDto);

        Seller seller = sellerService.register(registerDto);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New Seller account has been created successfully")
                .data(SellerResponseDto.build(seller))
                .path(request.getRequestURI())
                .build()
        );
    }
}
