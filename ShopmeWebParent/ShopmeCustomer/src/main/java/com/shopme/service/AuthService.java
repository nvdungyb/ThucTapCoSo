package com.shopme.service;

import com.shopme.Reposistory.UserRepository;
import com.shopme.advice.exception.InvalidCredentialsException;
import com.shopme.common.entity.User;
import com.shopme.dto.request.RefreshTokenDto;
import com.shopme.security.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@Service
public class AuthService {
    private final RedisService redisService;
    @Value("${accessTokenValidity}")
    private long ACCESS_TOKEN_VALIDITY;

    @Value("${refreshTokenValidity}")
    private long REFRESH_TOKEN_VALIDITY;

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
    }

    public Map<String, String> authenticateAndGenerateTokens(String email, String password) throws InvalidCredentialsException {
        User user = checkCredentials(email, password)
                .orElseThrow(() -> new InvalidCredentialsException("Email or password is incorrect"));

        return generateJwtTokens(user);
    }

    public Map<String, String> generateJwtTokens(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(user.getId()), REFRESH_TOKEN_VALIDITY);

        return Map.of(ACCESS_TOKEN_NAME, accessToken, REFRESH_TOKEN_NAME, refreshToken);
    }

    private Optional<User> checkCredentials(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Prevent timing attack
        String encodedPassword = userOptional.map(u -> u.getPassword())
                .orElse(UUID.randomUUID().toString());

        if (passwordEncoder.matches(password, encodedPassword)) {
            return userOptional;
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

        Long userId = jwtUtils.getUserIdFromRefreshToken(refreshTokenDto.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!invalidateRefreshToken(refreshTokenDto.getRefreshToken())) {
            throw new InvalidCredentialsException("Can not save this refresh token in redis");
        }

        return generateJwtTokens(user);
    }

    public boolean invalidateRefreshToken(String token) {
        return redisService.saveInvalidRefreshToken(token);
    }

    public boolean isEmailUnique(Long id, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty())
            return true;

        boolean isCreatingNew = (id == null);
        if (isCreatingNew || userOptional.map(User::getId).get() != id) {
            return false;
        }
        return true;
    }
}