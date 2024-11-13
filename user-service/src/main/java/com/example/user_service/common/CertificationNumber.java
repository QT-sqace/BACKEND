package com.example.user_service.common;

//인증 메일 숫자 지정
public class CertificationNumber {

    public static String getCertificationNumber() {
        String certificationNumber = "";

        for (int count = 0; count < 6; count++) certificationNumber += (int) (Math.random() * 10);

        return certificationNumber;
    }
}
