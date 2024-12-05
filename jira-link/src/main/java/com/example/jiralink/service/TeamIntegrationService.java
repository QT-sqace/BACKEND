package com.example.jiralink.service;

import com.example.jiralink.dto.TeamResponse;
import org.springframework.stereotype.Service;

@Service
public class TeamIntegrationService {

    public TeamResponse getTeam(Long teamId) {
        // 팀 서비스와 통신해서 팀 정보를 가져오는 로직
        // 이 코드는 Mock으로 처리. 실제 서비스 호출은 FeignClient 등을 사용.
        TeamResponse response = new TeamResponse();
        response.setId(teamId);
        response.setName("Example Team");
        response.setDescription("Example team description");
        return response;
    }
}