package com.example.user_service.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

//인증메일 형식 제공자
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "[Sqace 서비스] 인증메일입니다.";

    public boolean sendCertificationMail(String email, String certificationNumber) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email); //보낼 이메일 주소
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;

    }

    //인증메일 형식 지정
    private String getCertificationMessage (String certificationNumber) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>[Sqace 서비스] 인증메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;
    }
}
