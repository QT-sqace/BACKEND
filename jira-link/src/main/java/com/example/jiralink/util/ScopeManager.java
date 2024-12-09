package com.example.jiralink.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScopeManager {

    @Value("${jira.scopes}")
    private List<String> scopes;

    @Getter
    private String scopeString;

    @PostConstruct
    public void init() {
        scopeString = String.join("%20", scopes);
        System.out.println("Generated scope string: " + scopeString); // 디버깅용 로그
    }

}

