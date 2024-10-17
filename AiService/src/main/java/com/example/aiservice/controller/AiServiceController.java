package com.example.aiservice.controller;

import com.example.aiservice.service.STTService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class AiServiceController {
    private final STTService STTService;

    public AiServiceController(STTService STTService) {
        this.STTService = STTService;
    }

    @PostMapping("/stt")
    public String index(@RequestParam("file") MultipartFile multipartFile) throws URISyntaxException, IOException {
        // MultipartFile을 File로 변환
        File file = convertToFile(multipartFile);
        return STTService.stt(file);
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        // 임시 파일 생성
        File tempFile = File.createTempFile("uploaded", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
}
