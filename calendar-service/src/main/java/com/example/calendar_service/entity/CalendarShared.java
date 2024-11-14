package com.example.calendar_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calendar_shared")
@Getter
@NoArgsConstructor
public class CalendarShared {
    //개인 캘린더에서 팀 일정가져오기 위한 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharedId;

    @Column(nullable = false)
    private Long calendarId;    //개인 캘린더ID

    @Column(nullable = false)
    private Long eventId;   //팀 이벤트 ID
}
