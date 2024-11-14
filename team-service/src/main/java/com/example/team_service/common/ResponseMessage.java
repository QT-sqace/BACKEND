package com.example.team_service.common;

//응답 형식 메시지
public interface ResponseMessage {

    String SUCCESS = "Success.";

    String VALIDATION_FAIL = "Validation failed.";
    String DUPLICATE_ID = "DUPLICATE Id.";

    String SIGN_IN_FAIL = "Login information mismatch.";
    String CERTIFICATION_FAIL = "Certification failed.";

    String MAIL_FAIL = "Mail send failed.";
    String DATABASE_ERROR = "Database error.";
}