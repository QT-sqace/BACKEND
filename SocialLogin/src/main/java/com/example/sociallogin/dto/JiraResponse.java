package com.example.sociallogin.dto;

import java.util.Map;

public class JiraResponse implements OAuth2Response {
    private final Map<String, Object> attributes;

    public JiraResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "jira";
    }

    @Override
    public String getProviderId() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    /**
     *
     * @return jira 유저 고유 값
     * @throws IllegalArgumentException getName의 메소드 값으로 account_id가 없을 경우와 getName메소드를 구현하지 않을 경우
     *
     */
    @Override
    public String getName() {
        return attributes.get("account_id").toString();
    }
}
