package com.jpcode.screentrack.email;


import com.jpcode.screentrack.exception.BusinessRuleException;
import com.jpcode.screentrack.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private static final String EMAIL_ORIGIN = "screentrack@email.com";
    private static final String NAME_SENDER = "Screen Track Admin";

    public static final String URL_SITE = "http://localhost:8080"; 

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    @Async
    private void sendEmail(String userEmail, String subject, String content) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(EMAIL_ORIGIN, NAME_SENDER);
            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch(MessagingException | UnsupportedEncodingException e){
            throw new BusinessRuleException("Error sending email");
        }

        emailSender.send(message);
    }

    public void sendEmailVerification(User user) {
        String subject = "Here is your link to verify your email";
        String context = generateEmailContent("Hello [[name]],<br>"
                + "Please click the link below to verify your account:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Screen Track :).", user.getName(), URL_SITE + "/verify-account?code=" + user.getTokenVerification());
        sendEmail(user.getUsername(), subject, context);
    }

    private String generateEmailContent(String template, String name, String url) {
        return template.replace("[[name]]", name).replace("[[URL]]", url);
    }
}

