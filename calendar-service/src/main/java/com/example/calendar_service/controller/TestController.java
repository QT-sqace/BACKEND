package com.example.calendar_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    //나중에 배포후 확인용도
    //로컬기준 localhost:8000/calendarservice/health_check
    @GetMapping("/health_check")
    public String healthCheck() {
        log.info("호출 완료");
        return "OK";
    }
}
