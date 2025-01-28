package com.shopme.authentication;

import com.shopme.advice.exception.RedisFailureException;
import com.shopme.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
public class SessionService {

    private final RedisService redisService;

    public SessionService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Initialize a session for the user, storing refresh token in Redis, user ip for security. (just for testing)
     * In a real-world scenario, we would use both user-agent, device id, etc
     *
     * @param email
     * @param refreshToken
     * @param request
     */
    public void initializeSession(@Email String email, String refreshToken, HttpServletRequest request) {
        String userIp = request.getHeader("X-Forwarded-For");
        if (userIp == null || userIp.isEmpty() || "unknown".equalsIgnoreCase(userIp)) {
            userIp = request.getHeader("X-Real-IP");
        }
        if (userIp == null || userIp.isEmpty() || "unknown".equalsIgnoreCase(userIp)) {
            userIp = request.getRemoteAddr();
        }
        redisService.saveUserSession(refreshToken, email, userIp);
    }
}
