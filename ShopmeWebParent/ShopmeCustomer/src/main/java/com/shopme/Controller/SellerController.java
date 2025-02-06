package com.shopme.Controller;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.common.dto.ApiResponse;
import com.shopme.common.entity.Seller;
import com.shopme.dto.request.SellerRegisterDto;
import com.shopme.dto.response.SellerResponseDto;
import com.shopme.service.SellerService;
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
    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
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
}
