package com.shopme.service;

import com.shopme.advice.exception.InvalidCredentialsException;
import com.shopme.common.entity.Customer;
import com.shopme.Reposistory.CustomerRepository;
import com.shopme.message.dto.request.RefreshTokenDto;
import com.shopme.security.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
public class AuthService {
    private final RedisService redisService;
    @Value("${accessTokenValidity}")
    private long ACCESS_TOKEN_VALIDITY;

    @Value("${refreshTokenValidity}")
    private long REFRESH_TOKEN_VALIDITY;

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";

    private final JwtUtils jwtUtils;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtils jwtUtils, CustomerRepository customerRepository, PasswordEncoder passwordEncoder, RedisService redisService) {
        this.jwtUtils = jwtUtils;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
    }

    public Map<String, String> authenticateAndGenerateTokens(String email, String password) throws InvalidCredentialsException {
        Customer customer = checkCredentials(email, password)
                .orElseThrow(() -> new InvalidCredentialsException("Email or password is incorrect"));

        return generateJwtTokens(customer.getUser().getEmail(), customer.getId());
    }

    public Map<String, String> generateJwtTokens(String email, long customerId) {
        String accessToken = jwtUtils.generateAccessToken(email, ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(customerId), REFRESH_TOKEN_VALIDITY);

        return Map.of(ACCESS_TOKEN_NAME, accessToken, REFRESH_TOKEN_NAME, refreshToken);
    }

    private Optional<Customer> checkCredentials(String email, String password) {
        Optional<Customer> customerOptional = customerRepository.findByUserEmail(email);

        // Prevent timing attack
        String encodedPassword = customerOptional.map(c -> c.getUser().getPassword())
                .orElse(UUID.randomUUID().toString());

        if (passwordEncoder.matches(password, encodedPassword)) {
            return customerOptional;
        } else {
            return Optional.empty();
        }
    }

    public Map<String, String> generateTokens(RefreshTokenDto refreshTokenDto) throws InvalidCredentialsException {
        if (redisService.getInvalidRefreshToken(refreshTokenDto.getRefreshToken()).isPresent()) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        if (!jwtUtils.isValidRefreshToken(refreshTokenDto.getRefreshToken())) {
            throw new InvalidCredentialsException("Refresh token is invalid or expired");
        }

        Long customerId = jwtUtils.getCustomerIdFromRefreshToken(refreshTokenDto.getRefreshToken());
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidCredentialsException("Customer not found"));

        if (!invalidateRefreshToken(refreshTokenDto.getRefreshToken())) {
            throw new InvalidCredentialsException("Can not save this refresh token in redis");
        }

        return generateJwtTokens(customer.getUser().getEmail(), customer.getId());
    }

    public boolean invalidateRefreshToken(String token) {
        return redisService.saveInvalidRefreshToken(token);
    }
}