package com.shopme.customer;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.InvalidTokenException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.advice.exception.TooManyRequestsException;
import com.shopme.common.entity.Customer;
import com.shopme.mail.MailService;
import com.shopme.message.dto.request.CustomerRegisterDto;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.request.EmailCheckDto;
import com.shopme.message.dto.response.CustomerResponseDto;
import com.shopme.verification.RegisterVerification;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final RegisterVerification registerVerification;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService, RegisterVerification registerVerification) {
        this.customerService = customerService;
        this.registerVerification = registerVerification;
    }

    @PostMapping("/customer/save")
    public @ResponseBody Customer saveCustomer(@RequestBody Customer customer, RedirectAttributes redirectAttributes) throws IOException {

        Customer savedCustomer = customerService.save(customer);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return savedCustomer;
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

    @PostMapping("/customers/email/verification")
    public ResponseEntity<?> verificationEmail(@RequestBody Map<String, String> requestBody) throws TooManyRequestsException {
        String email = requestBody.get("email");
        logger.info("Email: {}", email);
        boolean isValidEmail = MailService.isValidEmail(email);
        if (isValidEmail) {
            registerVerification.sendVerificationToken(email);
        }

        return ResponseEntity.status(isValidEmail ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isValidEmail ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isValidEmail ? "Verification email has been sent to " + email : "Invalid email address")
                        .data(null)
                        .path("/customers/email/verification")
                        .build());
    }

    @GetMapping("/customers/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) throws InvalidTokenException {
        registerVerification.verifyToken(token);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Email has been verified successfully")
                .data(null)
                .path("/customers/email/verify")
                .build());
    }

    @PostMapping("/customers/email/uniqueness")
    public ResponseEntity<?> checkDuplicateEmail(@RequestBody EmailCheckDto emailCheckDto) {
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
