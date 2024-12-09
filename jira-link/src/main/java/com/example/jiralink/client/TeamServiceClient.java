package com.example.jiralink.client;

import com.example.jiralink.dto.MemberResponse;
import com.example.jiralink.dto.TeamResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "team-service", url = "${team-service.url}")
public interface TeamServiceClient {

    @GetMapping("/team/{teamId}")
    TeamResponse getTeam(@PathVariable("teamId") Long teamId);

    @GetMapping("/team/{teamId}/members")
    List<MemberResponse> getTeamMembers(@PathVariable("teamId") Long teamId);
}
