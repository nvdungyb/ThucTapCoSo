package com.dzungyb.security.advice.exception;

public class RedisFailureException extends Exception {
    public RedisFailureException(String message) {
        super(message);
    }
}
