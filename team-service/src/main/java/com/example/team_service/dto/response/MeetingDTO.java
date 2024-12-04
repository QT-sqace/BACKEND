package com.example.team_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MeetingDTO {
    private Long meetingId;
    private Long teamId;
    private Long createdBy; // 작성자 ID
    private String meetingName;
    private String meetingUrl;
    private String userName; // 작성자 이름 추가

    @JsonFormat(pattern = "yyyy-MM-dd") // 날짜 포맷 지정
    private LocalDateTime meetingDate;
}
