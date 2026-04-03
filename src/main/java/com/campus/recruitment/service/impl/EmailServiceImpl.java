package com.campus.recruitment.service.impl;

import com.campus.recruitment.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender emailSender;

    @Value("${app.email.enabled}")
    private boolean isEmailEnabled;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (!isEmailEnabled) {
            logger.info("Email disabled. Simulating email send to: {}, Subject: {}, Body: {}", to, subject, body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isEmpty()) {
                message.setFrom(fromEmail);
            } else {
                message.setFrom("noreply@campus.com");
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            emailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
}
