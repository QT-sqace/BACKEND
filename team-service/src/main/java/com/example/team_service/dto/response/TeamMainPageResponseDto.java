package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeamMainPageResponseDto {

    private List<MemberInfoDto> members;
    private List<NoticeInfoDto> notices;
    private List<CalendarInfoDto> calendars;
}
