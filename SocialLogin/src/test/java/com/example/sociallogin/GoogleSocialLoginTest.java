package com.example.sociallogin;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoogleSocialLoginTest {
    private static final Logger log = LoggerFactory.getLogger(GoogleSocialLoginTest.class);
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    private String redirectUri="https://7bf4-122-45-247-214.ngrok-free.app/login/oauth2/code/kakao";
    private final String scope = "openid";
    String ssdf="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=a5af70893bd768abd84af9ccf934ddb7&redirect_uri=https://7bf4-122-45-247-214.ngrok-free.app/kakao";
    @Test
    void contextLoads() {
        String uri = authorizationUri + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code&scope=" + scope;
        log.info("uri: {}", uri);
    }
}
