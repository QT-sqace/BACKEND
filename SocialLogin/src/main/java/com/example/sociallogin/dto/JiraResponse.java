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

    @Override
    public String getName() {
        return attributes.get("account_id").toString();
    }
}
