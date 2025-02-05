package com.dzungyb.security.advice.exception;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
