package com.example.team_service.common;

//응답 형식 코드
public interface ResponseCode {

    String SUCCESS = "SU";

    String VALIDATION_FAIL = "VF";
    String DUPLICATE_ID = "DI";

    String SIGN_IN_FAIL = "SF";
    String CERTIFICATION_FAIL = "CF";

    String MAIL_FAIL = "MF";
    String DATABASE_ERROR = "DBE";

}
