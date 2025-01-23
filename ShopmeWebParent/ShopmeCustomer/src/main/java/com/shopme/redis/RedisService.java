package com.shopme.redis;

import com.shopme.advice.exception.RedisFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Service
public class RedisService {
    @Value("${redis_key_ttl}")
    private long ttl;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String prefixValidationKey = "validation";
    private static final String prefixTimestampKey = "timestamp";

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String email, String token) throws RedisFailureException {
        saveToHSet(token, email, this::genValidationKey);
    }

    public String getEmailByToken(String token) {
        String key = genValidationKey(token);
        String email = (String) redisTemplate.opsForHash().get(key, token);
        if (email == null) {
            log.warn("Token not found or expired: {}", token);
        }
        return email;
    }

    private String genValidationKey(String token) {
        return prefixValidationKey + ":" + token;
    }

    public void deleteToken(String token) {
        String key = genValidationKey(token);
        redisTemplate.opsForHash().delete(key, token);
    }

    // todo: Token should be encrypted.
    // todo: Performance should use redis pipeline.
    private void saveToHSet(String hashKey, Object value, Function<String, String> genKeyFunc) throws RedisFailureException {
        String key = genKeyFunc.apply(hashKey);
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
            log.info("Saved to redis: key={}, hashKey={}, value={}", key, hashKey, value);
        } catch (Exception e) {
            log.error("Failed to save to redis: key={}, hashKey={}, value={}", key, hashKey, value);
            throw new RedisFailureException("Error saving to redis");
        }
    }

    public void saveLastSentTime(String email, String timestamp) throws RedisFailureException {
        saveToHSet(email, timestamp, this::genEmailSentTimestampKey);
    }

    private String genEmailSentTimestampKey(String email) {
        return prefixTimestampKey + ":" + email;
    }

    public String getLastSentTime(String email) {
        String key = genEmailSentTimestampKey(email);
        String lastTime = (String) redisTemplate.opsForHash().get(key, email);
        if (lastTime == null) {
            log.warn("No timestamp found for email: {}", email);
        }
        return lastTime;
    }
}
