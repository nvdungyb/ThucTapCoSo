package com.shopme.message.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LogoutDto {
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
