package com.shopme.mail;

import com.shopme.Reposistory.UserRepository;
import com.shopme.advice.exception.InvalidTokenException;
import com.shopme.advice.exception.RedisFailureException;
import com.shopme.advice.exception.TooManyRequestsException;
import com.shopme.common.entity.User;
import com.shopme.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterVerification {
    private final MailService mailService;
    private final RedisService redisService;
    private final RegisterTokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    @Value("${rate_limit_sent_time}")
    private long rateLimitSentTime;

    public RegisterVerification(MailService mailService, RedisService redisService, RegisterTokenGenerator tokenGenerator, UserRepository userRepository) {
        this.mailService = mailService;
        this.redisService = redisService;
        this.tokenGenerator = tokenGenerator;
        this.userRepository = userRepository;
    }

    public void sendVerificationToken(String email) throws TooManyRequestsException, RedisFailureException {
        String lastSentTime = redisService.getLastSentTime(email);

        if (lastSentTime != null && withinRateLimit(lastSentTime)) {
            throw new TooManyRequestsException("Too many requests. Please try again later");
        }

        String token = tokenGenerator.generateUUIDToken();
        mailService.sendEmailVerification(email, token);
        redisService.saveRegisterToken(email, token);
        redisService.saveLastSentTime(email, String.valueOf(System.currentTimeMillis()));
    }

    private boolean withinRateLimit(String lastSentTime) {
        long lastTime = Long.parseLong(lastSentTime);
        return (System.currentTimeMillis() - lastTime) < rateLimitSentTime;
    }

    public void verifyToken(String token) throws InvalidTokenException {
        String email = redisService.getEmailByToken(token);
        if (email == null)
            throw new InvalidTokenException("Invalid token or token expired");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        if (user.isEnabled())
            throw new InvalidTokenException("User already verified");

        user.setEnabled(true);
        userRepository.save(user);
        redisService.deleteToken(token);
    }
}
