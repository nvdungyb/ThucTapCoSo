package com.shopme.verification;

import com.shopme.advice.exception.FailedToUpdatePasswordException;
import com.shopme.advice.exception.RedisFailureException;
import com.shopme.customer.CustomerController;
import com.shopme.mail.RegisterVerification;
import com.shopme.message.ApiResponse;
import com.shopme.message.dto.request.AuthCodeDto;
import com.shopme.message.dto.request.ForgotPasswordDto;
import com.shopme.message.dto.request.ResetPasswordDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.shopme.mail.MailService.maskEmail;

@RestController
public class VerificationController {
    private final RegisterVerification registerVerification;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final ForgotPasswordService forgotPasswordService;

    public VerificationController(RegisterVerification registerVerification, ForgotPasswordService forgotPasswordService) {
        this.registerVerification = registerVerification;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/customers/password/reset")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) throws RedisFailureException {
        forgotPasswordService.forgotPassword(forgotPasswordDto.getEmail());

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Auth code has been sent to " + maskEmail(forgotPasswordDto.getEmail()))
                .data(null)
                .path("customers/password/reset")
                .build());
    }

    @PostMapping("/customers/password/reset/verify")
    public ResponseEntity<?> verifyAuthCode(@Valid @RequestBody AuthCodeDto authCodeDto) throws RedisFailureException {
        boolean isValid = forgotPasswordService.verifyAuthCode(authCodeDto.getEmail(), authCodeDto.getAuthCode());
        String resetPasswordToken = null;
        if (isValid) {
            forgotPasswordService.invalidateAuthCode(authCodeDto.getEmail());
            resetPasswordToken = forgotPasswordService.generateAndSaveResetToken(authCodeDto.getEmail());
        }

        ResponseCookie cookie = ResponseCookie.from("resetToken", isValid ? resetPasswordToken : "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict") // Ngăn chặn CSRF
                .path("/")
                .maxAge(isValid ? Duration.ofMinutes(15) : Duration.ofSeconds(0))
                .build();

        return ResponseEntity.status(isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isValid ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isValid ? "Auth code is valid" : "Invalid email address or auth code")
                        .data(null)
                        .path("/customers/password/reset/verify")
                        .build());
    }

    @PostMapping("/customers/password/reset/new")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) throws FailedToUpdatePasswordException {
        boolean isReset = forgotPasswordService.resetPassword(resetPasswordDto.getResetToken(), resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword());

        return ResponseEntity.status(isReset ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isReset ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isReset ? "Password has been reset successfully" : "Failed to reset password")
                        .data(null)
                        .path("/customers/password/reset/new")
                        .build());
    }
}
