package com.example.calendar_service.client;

import com.example.calendar_service.dto.external.TeamMemberResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "team-service-client", url = "http://team-service.spring-boot-app.svc.cluster.local:8084")
public interface TeamServiceClient {

    //팀 일정 등록시점에 팀 서비스로 팀에 속한 userId가져오기
    @GetMapping("/teams/members/{teamId}")
    TeamMemberResponseDto getTeamMembers(@PathVariable("teamId") Long teamId);
}
