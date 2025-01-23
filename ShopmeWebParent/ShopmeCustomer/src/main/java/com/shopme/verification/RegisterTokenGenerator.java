package com.shopme.verification;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegisterTokenGenerator {
    public String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }
}
