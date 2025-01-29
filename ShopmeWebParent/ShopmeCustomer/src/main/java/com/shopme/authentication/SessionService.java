package com.shopme.authentication;

import com.shopme.message.dto.request.LogoutDto;
import com.shopme.redis.RedisService;
import com.shopme.security.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
public class SessionService {

    private final RedisService redisService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SessionService.class);

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

    public boolean invalidateSession(@Valid LogoutDto logoutDto, HttpServletResponse response) {
        String refreshToken = logoutDto.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            logger.warn("Logout request failed: Refresh token is null or empty");
            return false;
        }

        redisService.deleteUserSession(logoutDto.getRefreshToken());
        logger.info("Successfully deleted session for user.");

        String accessToken = logoutDto.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            Cookie accessTokenCookie = CookieUtil.deleteCookie(AuthService.ACCESS_TOKEN_NAME, logoutDto.getAccessToken());
            response.addCookie(accessTokenCookie);
        }

        Cookie refreshTokenCookie = CookieUtil.deleteCookie(AuthService.REFRESH_TOKEN_NAME, logoutDto.getRefreshToken());
        response.addCookie(refreshTokenCookie);
        logger.info("Successfully invalidated session and deleted cookies.");

        return true;
    }
}
