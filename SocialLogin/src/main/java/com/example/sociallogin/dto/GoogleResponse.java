package com.example.sociallogin.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private final Map<String,Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    /**
     * @return 구글 이메일
     */
    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    /**
     * @return 구글 유저 실명
     */
    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
