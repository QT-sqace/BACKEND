package com.example.aiservice.controller;

import com.example.aiservice.service.STTService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URISyntaxException;

@RestController
public class AiServiceController {
    private final STTService STTService;

    public AiServiceController(STTService STTService) {
        this.STTService = STTService;
    }

    @PostMapping("/stt")
    public String index(@RequestParam("file") File file) throws URISyntaxException {
        return STTService.stt(file);
    }
}
