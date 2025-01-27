package com.shopme.authentication;

import com.shopme.advice.exception.InvalidCredentialsException;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerRepository;
import com.shopme.security.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Getter
public class AuthService {
    @Value("${accessTokenValidity}")
    private long ACCESS_TOKEN_VALIDITY;

    @Value("${refreshTokenValidity}")
    private long REFRESH_TOKEN_VALIDITY;

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";

    private final JwtUtils jwtUtils;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtils jwtUtils, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> authenticateAndGenerateTokens(String email, String password) throws InvalidCredentialsException {
        if (!checkCredentials(email, password)) {
            throw new InvalidCredentialsException("Email or password is incorrect");
        }

        return generateJwtTokens(email);
    }

    private Map<String, String> generateJwtTokens(String email) {
        String accessToken = jwtUtils.generateAccessToken(email, ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtUtils.generateRefreshToken(REFRESH_TOKEN_VALIDITY);

        return Map.of(ACCESS_TOKEN_NAME, accessToken, REFRESH_TOKEN_NAME, refreshToken);
    }

    private boolean checkCredentials(String email, String password) {
        Optional<Customer> customerOptional = customerRepository.findByUserEmail(email);

        if (customerOptional.isEmpty()) {
            return false;
        }

        String encodedPassword = customerOptional.get().getUser().getPassword();
        return passwordEncoder.matches(password, encodedPassword);
    }
}
