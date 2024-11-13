package com.example.calendar_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/health_check")
    public String healthCheck() {
        log.info("호출 완료");
        return "OK";
    }
}
