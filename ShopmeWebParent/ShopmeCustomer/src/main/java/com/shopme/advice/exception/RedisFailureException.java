package com.shopme.advice.exception;

public class RedisFailureException extends Exception {
    public RedisFailureException(String message) {
        super(message);
    }
}
