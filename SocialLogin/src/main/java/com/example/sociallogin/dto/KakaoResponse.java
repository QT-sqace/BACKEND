package com.example.sociallogin.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return (String) attribute.get("sub");
    }

    /**
     * 현재 카카오 앱 인증을 받지 않아서 이메일을 받아올 수 없음
     *
     * @return 카카오 이메일
     */
    @Override
    public String getEmail() {
        return attribute.get("account_email").toString();
    }

    /**
     * 현재 카카오 앱 인증을 받지 않아서 실명을 받아올 수 없음
     *
     * @return 카카오 유저 실명
     */
    @Override
    public String getName() {
        return "";
    }


    /**
     * @return 카카오 프로필 닉네임
     */
    public String getNickName() {
        return attribute.get("profile_nickname").toString();
    }
}
