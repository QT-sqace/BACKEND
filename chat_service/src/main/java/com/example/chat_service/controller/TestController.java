package com.example.chat_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //경로 확인용도 http://localhost:8083/test/health_check
    @GetMapping("/test/health_check")
    public String healthCheck() {
        return "OK";
    }
}
