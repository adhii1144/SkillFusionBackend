package com.example.talenttrove.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConnectionRequest(String fromUser, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromUser);
        message.setTo(toEmail);
        message.setSubject("New Connection Request");
        message.setText("Hello,\n\n" + fromUser + " has sent you a connection request. Accept it from your dashboard.");

        mailSender.send(message);
    }

    public void sendStatusUpdate(String toEmail, String fromUser, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromUser);
        message.setTo(toEmail);
        message.setSubject("Connection Request " + status);
        message.setText("Hello,\n\nYour connection request from " + fromUser + " has been " + status + ".");

        mailSender.send(message);
    }
}
