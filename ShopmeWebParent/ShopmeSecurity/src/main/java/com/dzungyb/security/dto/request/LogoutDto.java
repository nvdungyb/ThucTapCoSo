package com.dzungyb.security.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class LogoutDto {
    @NotBlank
    private String refreshToken;
}


