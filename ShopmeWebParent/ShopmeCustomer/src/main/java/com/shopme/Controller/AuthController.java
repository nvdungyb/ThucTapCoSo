package com.shopme.Controller;

import com.shopme.advice.exception.InvalidCredentialsException;
import com.shopme.common.dto.ApiResponse;
import com.shopme.dto.request.LoginDto;
import com.shopme.dto.request.LogoutDto;
import com.shopme.dto.request.RefreshTokenDto;
import com.shopme.service.AuthService;
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

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response, HttpServletRequest request) throws InvalidCredentialsException {
        logger.info("Login DTO: {}", loginDto);

        Map<String, String> tokens = authService.authenticateAndGenerateTokens(loginDto.getEmail(), loginDto.getPassword());

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(tokens)
                .path("/auth/login")
                .build());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutDto logoutDto) {
        logger.info("Logout DTO: {}", logoutDto);

        boolean isInvalidated = authService.invalidateRefreshToken(logoutDto.getRefreshToken());

        return ResponseEntity.status(isInvalidated ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isInvalidated ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isInvalidated ? "Logout successful" : "Can not save this refresh token in redis")
                        .data(null)
                        .path("/auth/logout")
                        .build());
    }

    @PostMapping("/auth/access-token")
    public ResponseEntity<?> getAccessToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto, HttpServletRequest request, HttpServletResponse response) throws InvalidCredentialsException {
        logger.info("Refresh token DTO: {}", refreshTokenDto);

        Map<String, String> tokens = authService.generateTokens(refreshTokenDto);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Access token generated successfully")
                .data(tokens)
                .path("/auth/access-token")
                .build());
    }
}
