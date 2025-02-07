package com.shopme.mail;

import com.shopme.advice.exception.InvalidTokenException;
import com.shopme.advice.exception.RedisFailureException;
import com.shopme.advice.exception.TooManyRequestsException;
import com.shopme.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.shopme.mail.MailService.maskEmail;

@RestController
public class MailController {
    private final RegisterVerification registerVerification;
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    public MailController(RegisterVerification registerVerification) {
        this.registerVerification = registerVerification;
    }

    @PostMapping("/email/verification")
    public ResponseEntity<?> verificationEmail(@RequestBody Map<String, String> requestBody) throws TooManyRequestsException, RedisFailureException {
        String email = requestBody.get("email");
        logger.info("Email: {}", maskEmail(email));
        boolean isValidEmail = MailService.isValidEmail(email);
        if (isValidEmail) {
            registerVerification.sendVerificationToken(email);
        }

        return ResponseEntity.status(isValidEmail ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isValidEmail ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isValidEmail ? "Verification email has been sent to " + email : "Invalid email address")
                        .data(null)
                        .path("/email/verification")
                        .build());
    }

    @GetMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) throws InvalidTokenException {
        registerVerification.verifyToken(token);

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Email has been verified successfully")
                .data(null)
                .path("/email/verify")
                .build());
    }
}
