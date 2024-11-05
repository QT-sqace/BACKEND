package com.example.team_service.controller;

import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.TestDto;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final UserServiceClient userServiceClient;
    private final JwtUtil jwtUtil;


    //경로 확인:http://localhost:8000/teamservice/user/health_check
    @GetMapping("/user/health_check")
    public String healthCheck() {
        return "OK";
    }

    //테스트 용도 팀 생성
    @PostMapping("/createTeam")
    public String createTeam(@RequestBody TestDto testDto,
                             @RequestHeader("Authorization") String token) {
        //헤더에서 토큰 가져오고, body에서 요청값 가져오기

        //Bearer 부분 제거
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractedUserId(jwt); //userId값 추출

        log.info("userID값 확인: {}", userId);
        //Feign Client로 userId값 통해서 필요한 user정보 가져오기
        //임시로 userName과 email이 필요하다고 가정
        String userName = userServiceClient.getUserBasicInfo(userId).getUserName();
        String email = userServiceClient.getUserBasicInfo(userId).getEmail();

        log.info("userId 값: {}, userName 값: {}, Email 값: {}", userId, userName, email);
        log.info("TeamName 값: {}, password 값: {}", testDto.getTeamName(), testDto.getPassword());


        return "OK";
    }

}
