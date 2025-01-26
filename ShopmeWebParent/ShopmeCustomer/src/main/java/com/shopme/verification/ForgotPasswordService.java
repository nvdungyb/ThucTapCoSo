package com.shopme.verification;

import com.shopme.advice.exception.RedisFailureException;
import com.shopme.mail.MailService;
import com.shopme.redis.RedisService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.security.SecureRandom;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    private final MailService mailService;
    private final RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    public ForgotPasswordService(MailService mailService, RedisService redisService) {
        this.mailService = mailService;
        this.redisService = redisService;
    }

    public void forgotPassword(String email) throws RedisFailureException {
        String authCode = generateAuthCode();
        try {
            redisService.saveAuthCode(email, authCode);
            mailService.sendAuthCodeForgotPassword(email, authCode);
        } catch (RedisFailureException e) {
            logger.error(e.getMessage());
            throw new RedisFailureException("Error saving to redis");
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            redisService.deleteAuthCode(email);
            throw new RuntimeException(e);
        }
    }

    private String generateAuthCode() {
        SecureRandom random = new SecureRandom();
        return String.valueOf((random.nextInt(9000) + 1000));
    }

    public boolean verifyAuthCode(String email, String authCode) {
        String savedAuthCode = redisService.getAuthCode(email);
        if (savedAuthCode == null) {
            logger.warn("Auth code not found or expired: {}", email);
            return false;
        }

        boolean isValid = constantTimeEquals(savedAuthCode, authCode);
        if (!isValid) {
            logger.warn("Invalid auth code: {}", email);
        }

        return isValid;
    }

    // Using constant-time comparison to prevent timing attacks.
    private boolean constantTimeEquals(String a, String b) {
        int maxLength = Math.max(a.length(), b.length());
        int result = 0;
        for (int i = 0; i < maxLength; i++) {
            char charA = i < a.length() ? a.charAt(i) : 0;
            char charB = i < b.length() ? b.charAt(i) : 0;
            result |= charA ^ charB;
        }
        return result == 0;
    }

    public String generateAndSaveResetToken(@Email String email) throws RedisFailureException {
        String token = generateResetToken();
        try {
            redisService.saveChangePasswordToken(email, token);
        } catch (RedisFailureException e) {
            logger.error(e.getMessage());
            throw new RedisFailureException("Error saving to redis");
        }
        return token;
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public void invalidateAuthCode(@Email String email) {
        redisService.deleteAuthCode(email);
    }
}
