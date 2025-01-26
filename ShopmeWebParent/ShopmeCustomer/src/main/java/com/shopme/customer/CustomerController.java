package com.shopme.customer;

import com.shopme.advice.exception.*;
import com.shopme.common.entity.Customer;
import com.shopme.mail.MailService;
import com.shopme.message.dto.request.AuthCodeDto;
import com.shopme.message.dto.request.CustomerRegisterDto;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.request.EmailCheckDto;
import com.shopme.message.dto.request.ForgotPasswordDto;
import com.shopme.message.dto.response.CustomerResponseDto;
import com.shopme.redis.RedisService;
import com.shopme.verification.ForgotPasswordService;
import com.shopme.verification.RegisterVerification;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final RegisterVerification registerVerification;
    private final ForgotPasswordService forgotPasswordService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final RedisService redisService;

    public CustomerController(CustomerService customerService, RegisterVerification registerVerification, ForgotPasswordService forgotPasswordService, RedisService redisService) {
        this.customerService = customerService;
        this.registerVerification = registerVerification;
        this.forgotPasswordService = forgotPasswordService;
        this.redisService = redisService;
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
    public ResponseEntity<?> verificationEmail(@RequestBody Map<String, String> requestBody) throws TooManyRequestsException, RedisFailureException {
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

    // todo: add rate limiting.
    @PostMapping("customers/password/reset")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) throws RedisFailureException {
        forgotPasswordService.forgotPassword(forgotPasswordDto.getEmail());

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Auth code has been sent to " + forgotPasswordDto.getEmail())
                .data(null)
                .path("customers/password/reset")
                .build());
    }

    @PostMapping("/customers/password/reset/verify")
    public ResponseEntity<?> verifyAuthCode(@Valid @RequestBody AuthCodeDto authCodeDto) throws RedisFailureException {
        boolean isValid = forgotPasswordService.verifyAuthCode(authCodeDto.getEmail(), authCodeDto.getAuthCode());
        String resetPasswordToken = null;
        if (isValid) {
            forgotPasswordService.invalidateAuthCode(authCodeDto.getEmail());
            resetPasswordToken = forgotPasswordService.generateAndSaveResetToken(authCodeDto.getEmail());
        }

        ResponseCookie cookie = ResponseCookie.from("resetToken", isValid ? resetPasswordToken : "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // Ngăn chặn CSRF
                .path("/")
                .maxAge(isValid ? Duration.ofMinutes(15) : Duration.ofSeconds(0))
                .build();

        return ResponseEntity.status(isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isValid ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isValid ? "Auth code is valid" : "Invalid email address or auth code")
                        .data(null)
                        .path("/customers/password/reset/verify")
                        .build());
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
