//실제로 이메일을 보내는 역할
package com.example.team_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendInviteEmail(String toEmail, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("팀에 초대되었습니다!");
        message.setText("다음 링크를 클릭하여 팀에 가입하세요: " + inviteLink);
        mailSender.send(message);
    }
}
