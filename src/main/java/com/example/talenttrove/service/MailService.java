//package com.example.talenttrove.service;
//
//import com.example.talenttrove.dto.MailBody;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MailService {
//
//    private final JavaMailSender javaMailSender;
//
//    @Autowired
//    public MailService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//    public void sendSimpleMail(MailBody mailBody) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(mailBody.getTo());
//        message.setFrom("sunkeadarsh3@gmail.com");
//        message.setSubject(mailBody.getSubject());
//        message.setText(mailBody.getBody());
//        javaMailSender.send(message);
//    }
//}






package com.example.talenttrove.service;

import com.example.talenttrove.dto.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMail(MailBody mailBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            Context context = new Context();
            Map<String, Object> props = new HashMap<>();
            props.put("username", extractUsername(mailBody.getTo()));
            props.put("otp", extractOTP(mailBody.getBody()));
            context.setVariables(props);

            String htmlContent = templateEngine.process("password-reset-template", context);

            helper.setTo(mailBody.getTo());
            helper.setFrom("sunkeadarsh3@gmail.com");
            helper.setSubject(mailBody.getSubject());
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String extractUsername(String email) {
        return email.split("@")[0];
    }

    private String extractOTP(String body) {
        return body.replaceAll(".*: ", "");
    }
}