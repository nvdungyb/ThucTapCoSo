package com.security.security.mail;

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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MailService.class);

    // @Async annotation is used to run this method in a separate thread.
    @Async
    public void sendEmailVerification(String email, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/techshop/email/verify?token=" + token;
        String message = "<h2>Email Verification</h2>"
                + "<p>Click the link below to verify your email:</p>"
                + "<a href=\"" + verificationUrl + "\">Verify Email</a>";

        try {
            sendHtmlEmail(email, subject, message);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

    @Async
    public void sendAuthCodeForgotPassword(String email, String token) throws MessagingException {
        String subject = "Forgot Password";
        String message = "<h2>Forgot Password</h2>"
                + "<p>Use the code below to reset your password:</p>"
                + "<h3>" + token + "</h3>";

        sendHtmlEmail(email, subject, message);
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

    public static String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
