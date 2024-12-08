package com.example.calendar_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponseDto {
    //FeignClient - 팀 서비스로 팀 리스트 반환 받는 DTO
    private Long teamId;
    private List<TeamMemberInfoDto> members;
}
