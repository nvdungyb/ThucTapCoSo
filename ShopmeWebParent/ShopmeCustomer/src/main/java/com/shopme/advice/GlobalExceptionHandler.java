package com.shopme.advice;

import com.shopme.advice.exception.EmailAlreadyExistsException;
import com.shopme.advice.exception.RoleNotFoundException;
import com.shopme.message.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // https://www.baeldung.com/spring-boot-bean-validation for more information.
    @ExceptionHandler
    public ResponseEntity<?> handlerValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();
        bindingResult.getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Validation error")
                        .errors(errors)
                        .data(null)
                        .path("/customers/register")
                        .build()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .errors(Map.of("error", ex.getMessage()))
                        .path("/customers/register")
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> handleRoleNotFoundException(RoleNotFoundException ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .errors(Map.of("errors", ex.getMessage()))
                        .path("/customers/register")
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(null)
                        .data(null)
                        .build()
                );
    }

}
