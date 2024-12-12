package com.example.team_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NoticeInfoDto {
    //팀 메인에 활용할 공지

    private Long noticeId;
    private String noticeTitle;
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 날짜와 시간 모두 표시
    private LocalDateTime createdDate;
}
