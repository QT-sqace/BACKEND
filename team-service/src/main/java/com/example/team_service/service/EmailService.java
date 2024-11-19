package com.example.team_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    /**
     * 간단한 텍스트 이메일 발송
     *
     * @param toEmail    수신자 이메일
     * @param inviteLink 초대 링크
     */
    public void sendTextEmail(String toEmail, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("팀에 초대되었습니다!");
        message.setText("다음 링크를 클릭하여 팀에 가입하세요: " + inviteLink);

        try {
            mailSender.send(message);
            logger.info("텍스트 이메일 발송 성공: {}", toEmail);
        } catch (Exception e) {
            logger.error("텍스트 이메일 발송 실패: {}", e.getMessage());
        }
    }
}
