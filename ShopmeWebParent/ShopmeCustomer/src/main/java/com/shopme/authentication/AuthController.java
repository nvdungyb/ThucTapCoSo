package com.shopme.authentication;

import com.shopme.advice.exception.*;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.request.*;
import com.shopme.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/customers/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response, HttpServletRequest request) throws InvalidCredentialsException {
        logger.info("Login DTO: {}", loginDto);

        Map<String, String> tokens = authService.authenticateAndGenerateTokens(loginDto.getEmail(), loginDto.getPassword());

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(tokens)
                .path("/customers/login")
                .build());
    }

    @PostMapping("/customers/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutDto logoutDto) {
        logger.info("Logout DTO: {}", logoutDto);

        boolean isInvalidated = authService.invalidateRefreshToken(logoutDto.getRefreshToken());

        return ResponseEntity.status(isInvalidated ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isInvalidated ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isInvalidated ? "Logout successful" : "Can not save this refresh token in redis")
                        .data(null)
                        .path("/customers/logout")
                        .build());
    }

    @PostMapping("/customers/access-token")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto, HttpServletRequest request, HttpServletResponse response) throws InvalidCredentialsException {
        logger.info("Refresh token DTO: {}", refreshTokenDto);

        Map<String, String> tokens = authService.generateTokens(refreshTokenDto);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Access token generated successfully")
                .data(tokens)
                .path("/customers/access-token")
                .build());
    }
}
