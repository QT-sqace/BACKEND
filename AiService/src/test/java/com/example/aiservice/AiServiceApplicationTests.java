package com.example.aiservice;

import com.example.aiservice.service.STTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URISyntaxException;

@SpringBootTest
class AiServiceApplicationTests {

    @Autowired
    private STTService STTService;

    @Test
    void testSTT() throws URISyntaxException {
        File file = new File("C:\\Users\\hajew\\Downloads\\preprocessed_audio.wav");
        System.out.println(STTService.stt(file));
    }

}
