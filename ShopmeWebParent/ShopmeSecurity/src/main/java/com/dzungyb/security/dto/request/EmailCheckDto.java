package com.dzungyb.security.dto.request;

import lombok.Data;

import static com.dzungyb.security.mail.MailService.maskEmail;

@Data
public class EmailCheckDto {
    private Long id;
    private String email;

    public String toString() {
        return "EmailCheckDto{" +
                "id=" + id +
                ", email='" + maskEmail(email) + '\'' +
                '}';
    }
}
