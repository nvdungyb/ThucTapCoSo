package com.shopme.customer;

import com.shopme.advice.exception.*;
import com.shopme.authentication.AuthService;
import com.shopme.authentication.SessionService;
import com.shopme.common.entity.Customer;
import com.shopme.mail.MailService;
import com.shopme.message.dto.request.*;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.response.CustomerResponseDto;
import com.shopme.security.CookieUtil;
import com.shopme.verification.ForgotPasswordService;
import com.shopme.verification.RegisterVerification;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.shopme.mail.MailService.maskEmail;

@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final RegisterVerification registerVerification;
    private final ForgotPasswordService forgotPasswordService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final AuthService authService;
    private final SessionService sessionService;

    public CustomerController(CustomerService customerService, RegisterVerification registerVerification, ForgotPasswordService forgotPasswordService, AuthService authService, SessionService sessionService) {
        this.customerService = customerService;
        this.registerVerification = registerVerification;
        this.forgotPasswordService = forgotPasswordService;
        this.authService = authService;
        this.sessionService = sessionService;
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
        logger.info("Email: {}", maskEmail(email));
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
                .message("Auth code has been sent to " + maskEmail(forgotPasswordDto.getEmail()))
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

    @PostMapping("/customers/password/reset/new")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) throws FailedToUpdatePasswordException {
        boolean isReset = forgotPasswordService.resetPassword(resetPasswordDto.getResetToken(), resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword());

        return ResponseEntity.status(isReset ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isReset ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isReset ? "Password has been reset successfully" : "Failed to reset password")
                        .data(null)
                        .path("/customers/password/reset/new")
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

    @PostMapping("/customers/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response, HttpServletRequest request) throws InvalidCredentialsException {
        logger.info("Login DTO: {}", loginDto);

        Map<String, String> tokens = authService.authenticateAndGenerateTokens(loginDto.getEmail(), loginDto.getPassword());
        tokens.entrySet().stream().forEach(entry -> {
            long tokenValidity = entry.getKey().equals(AuthService.REFRESH_TOKEN_NAME) ? authService.getREFRESH_TOKEN_VALIDITY() : authService.getACCESS_TOKEN_VALIDITY();
            int maxAge = Math.toIntExact(tokenValidity / 1000);
            Cookie cookie = CookieUtil.createCookie(entry.getKey(), entry.getValue(), tokenValidity);
            response.addCookie(cookie);
            logger.info("Cookie created for key: {}", entry.getKey());
        });

        sessionService.initializeSession(loginDto.getEmail(), tokens.get(AuthService.REFRESH_TOKEN_NAME), request);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(maskEmail(loginDto.getEmail()))
                .path("/customers/login")
                .build());
    }
}
