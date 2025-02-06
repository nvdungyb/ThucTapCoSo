package com.shopme.Controller;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.dto.request.CustomerRegisterDto;
import com.shopme.dto.request.EmailCheckDto;
import com.shopme.common.entity.Customer;
import com.shopme.common.dto.ApiResponse;
import com.shopme.service.CustomerService;
import com.shopme.dto.response.CustomerResponseDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class CustomerController {
    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterDto registerDto) throws EmailAlreadyExistsException, RoleNotFoundException {
        logger.info("DTO: {}", registerDto);

        // Register customer.
        Customer customer = customerService.register(registerDto);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("New customer account has been created successfully")
                .data(CustomerResponseDto.build(customer))
                .path("/customers/register")
                .build()
        );
    }

    @PostMapping("/customers/email/uniqueness")
    public ResponseEntity<?> checkDuplicateEmail(@Valid @RequestBody EmailCheckDto emailCheckDto) {
        logger.info("EmailCheck: {}", emailCheckDto);

        Boolean isUnique = customerService.isEmailUnique(emailCheckDto.getId(), emailCheckDto.getEmail());
        ApiResponse response = ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(isUnique ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                .message(isUnique ? "Email is unique" : "Email is already used. Please choose another email")
                .data(null)
                .path("/customers/email/uniqueness")
                .build();

        return isUnique ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
