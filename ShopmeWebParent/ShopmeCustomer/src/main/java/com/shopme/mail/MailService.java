package com.shopme.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender mailSender;

    // @Async annotation is used to run this method in a separate thread.
    @Async
    public void sendEmailVerification(String email, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/techshop/customers/email/verify?token=" + token;
        String message = "<h2>Email Verification</h2>"
                + "<p>Click the link below to verify your email:</p>"
                + "<a href=\"" + verificationUrl + "\">Verify Email</a>";

        try {
            sendHtmlEmail(email, subject, message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(htmlContent, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(sender);

        mailSender.send(mimeMessage);
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }
}
