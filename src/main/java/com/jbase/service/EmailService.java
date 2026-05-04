package com.jbase.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private final JavaMailSender mailSender;
	
    public void sendOtp(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Seu código de acesso");
        message.setText("Seu código é: " + code);
        message.setFrom("michelhphz@gmail.com");

        mailSender.send(message);
    }
    
}
