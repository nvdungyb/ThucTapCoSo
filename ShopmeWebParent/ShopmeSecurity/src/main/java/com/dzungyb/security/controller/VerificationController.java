package com.dzungyb.security.controller;

import com.shopme.common.dto.ApiResponse;
import com.dzungyb.security.dto.request.AuthCodeDto;
import com.dzungyb.security.dto.request.ForgotPasswordDto;
import com.dzungyb.security.dto.request.ResetPasswordDto;
import com.dzungyb.security.service.ForgotPasswordService;
import com.dzungyb.security.advice.exception.FailedToUpdatePasswordException;
import com.dzungyb.security.advice.exception.RedisFailureException;
import com.dzungyb.security.mail.RegisterVerification;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.dzungyb.security.mail.MailService.maskEmail;

@RestController
public class VerificationController {
    private final RegisterVerification registerVerification;
    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);
    private final ForgotPasswordService forgotPasswordService;

    public VerificationController(RegisterVerification registerVerification, ForgotPasswordService forgotPasswordService) {
        this.registerVerification = registerVerification;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) throws RedisFailureException {
        forgotPasswordService.forgotPassword(forgotPasswordDto.getEmail());

        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .status(HttpStatus.OK.value())
                .message("Auth code has been sent to " + maskEmail(forgotPasswordDto.getEmail()))
                .data(null)
                .path("/password/reset")
                .build());
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<?> verifyAuthCode(@Valid @RequestBody AuthCodeDto authCodeDto) throws RedisFailureException {
        boolean isValid = forgotPasswordService.verifyAuthCode(authCodeDto.getEmail(), authCodeDto.getAuthCode());
        String resetPasswordToken = null;
        if (isValid) {
            forgotPasswordService.invalidateAuthCode(authCodeDto.getEmail());
            resetPasswordToken = forgotPasswordService.generateAndSaveResetToken(authCodeDto.getEmail());
        }

        return ResponseEntity.status(isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isValid ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isValid ? "Auth code is valid" : "Invalid email address or auth code")
                        .data(resetPasswordToken)
                        .path("/password/reset/verify")
                        .build());
    }

    @PostMapping("/password/reset/new")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) throws FailedToUpdatePasswordException {
        boolean isReset = forgotPasswordService.resetPassword(resetPasswordDto.getResetToken(), resetPasswordDto.getEmail(), resetPasswordDto.getNewPassword());

        return ResponseEntity.status(isReset ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .status(isReset ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .message(isReset ? "Password has been reset successfully" : "Failed to reset password")
                        .data(null)
                        .path("/password/reset/new")
                        .build());
    }
}
