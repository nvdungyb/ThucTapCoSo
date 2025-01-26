package com.shopme.verification;

import com.shopme.advice.exception.FailedToUpdatePasswordException;
import com.shopme.advice.exception.InvalidTokenException;
import com.shopme.advice.exception.RedisFailureException;
import com.shopme.common.entity.User;
import com.shopme.customer.CustomerService;
import com.shopme.mail.MailService;
import com.shopme.redis.RedisService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.security.SecureRandom;
import java.util.UUID;

import static com.shopme.mail.MailService.maskEmail;

@Service
public class ForgotPasswordService {
    private final MailService mailService;
    private final RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);
    private final CustomerService customerService;

    public ForgotPasswordService(MailService mailService, RedisService redisService, CustomerService customerService) {
        this.mailService = mailService;
        this.redisService = redisService;
        this.customerService = customerService;
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
            logger.warn("Auth code not found or expired: {}", maskEmail(email));
            return false;
        }

        boolean isValid = constantTimeEquals(savedAuthCode, authCode);
        if (!isValid) {
            logger.warn("Invalid auth code: {}", maskEmail(email));
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

    private boolean isValidResetToken(String email, String resetToken) {
        String savedToken = redisService.getChangePasswordToken(email);
        if (savedToken == null || !savedToken.equals(resetToken)) {
            logger.warn("Invalid or expired reset token for email: {}", maskEmail(email));
            return false;
        }
        return true;
    }

    private void updatePasswordAndInvalidateToken(String email, String newPassword) throws FailedToUpdatePasswordException {
        try {
            customerService.updatePassword(email, newPassword)
                    .orElseThrow(() -> new FailedToUpdatePasswordException("Failed to update password"));
            redisService.deleteChangePasswordToken(email);
        } catch (Exception ex) {
            logger.error("Error updating password for email: {}", maskEmail(email), ex);
            throw new FailedToUpdatePasswordException("Unable to update password.");
        }
    }

    public boolean resetPassword(@NotBlank String resetToken, @Email String email, @NotBlank String newPassword) throws FailedToUpdatePasswordException {
        if (!isValidResetToken(email, resetToken)) {
            return false;
        }

        updatePasswordAndInvalidateToken(email, newPassword);
        return true;
    }
}
