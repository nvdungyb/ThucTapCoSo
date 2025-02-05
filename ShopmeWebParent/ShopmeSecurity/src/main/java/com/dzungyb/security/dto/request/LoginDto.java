package com.dzungyb.security.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static com.dzungyb.security.mail.MailService.maskEmail;

@Data
public class LoginDto {
    @Email
    private String email;
    @NotBlank
    private String password;

    public String toString() {
        return "LoginDto{" +
                "email='" + maskEmail(email) + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
