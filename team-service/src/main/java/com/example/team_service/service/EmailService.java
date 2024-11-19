package com.example.team_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("팀에 초대되었습니다!");
        message.setText("다음 링크를 클릭하여 팀에 가입하세요: " + inviteLink);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // 로그 기록 또는 실패 처리
            System.out.println("이메일 발송 실패: " + e.getMessage());
        }

    }

}
