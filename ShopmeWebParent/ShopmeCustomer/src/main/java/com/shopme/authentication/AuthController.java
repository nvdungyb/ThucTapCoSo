package com.shopme.authentication;

import com.shopme.advice.exception.*;
import com.shopme.customer.CustomerController;
import com.shopme.mail.MailService;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.request.AuthCodeDto;
import com.shopme.message.dto.request.ForgotPasswordDto;
import com.shopme.message.dto.request.LoginDto;
import com.shopme.message.dto.request.ResetPasswordDto;
import com.shopme.security.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.shopme.mail.MailService.maskEmail;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final AuthService authService;
    private final SessionService sessionService;

    public AuthController(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
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
