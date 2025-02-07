package com.shopme.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthCodeDto {
    @Email
    private String email;
    @NotBlank
    private String authCode;
}
