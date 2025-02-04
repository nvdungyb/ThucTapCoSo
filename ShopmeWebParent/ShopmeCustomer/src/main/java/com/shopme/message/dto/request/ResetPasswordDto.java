package com.shopme.message.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordDto {
    @NotBlank
    private String resetToken;
    @Email
    private String email;
    @NotBlank
    private String newPassword;
}
