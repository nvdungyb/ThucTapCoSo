package com.dzungyb.security.advice;

import com.dzungyb.security.advice.exception.*;
import com.shopme.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private HttpServletRequest httpServletRequest;

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
                        .path(httpServletRequest.getRequestURI())
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
                        .path(httpServletRequest.getRequestURI())
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
                        .path(httpServletRequest.getRequestURI())
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
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Invalid or expired token")
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<?> handleTooManyRequestsException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.TOO_MANY_REQUESTS.value())
                        .message("Too many requests")
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(RedisFailureException.class)
    public ResponseEntity<?> handleRedisFailureException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.FAILED_DEPENDENCY.value())
                        .message("Redis failure")
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(FailedToUpdatePasswordException.class)
    public ResponseEntity<?> handleFailedToUpdatePasswordException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Failed to update password")
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(Exception ex) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(timestamp)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid credentials")
                        .errors(Map.of("errors", ex.getMessage()))
                        .path(httpServletRequest.getRequestURI())
                        .data(null)
                        .build()
                );
    }
}
