package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamListResponseDto {
    private Long teamId;                //팀 ID
    private String teamName;            //팀 이름
    private String teamImage;           //팀 이미지 경로
    private String role;                //팀 내 사용자 역할
    private int memberCount;            //팀 전체 인원수
    private List<String> memberImages;  //팀 회원들 이미지 경로들
}
