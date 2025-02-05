package com.dzungyb.security.advice.exception;

public class InvalidTokenException extends Exception {
    public InvalidTokenException(String message) {
        super(message);
    }
}
