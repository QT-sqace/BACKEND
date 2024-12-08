package com.example.calendar_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "calendar")
@Getter
@NoArgsConstructor
public class Calendar {
    //캘린더 소유자

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calendarId;

    @Column
    private Long userId;    //팀캘린더이면 null

    @Column
    private Long teamId;    //개인 캘린더이면 null

    //calendarID값이 사라지면 CalendarInfo의 데이터도 모두 삭제
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
    private List<CalendarInfo> calendarEvents;

    //개인 캘린더 요청시 사용
    public Calendar(Long userId) {
        this.userId = userId;
        this.teamId = null;
    }

    //팀 캘린더 요청시 사용
    public Calendar(Long teamId, boolean isTeamCalendar) {
        this.userId = null;
        this.teamId = teamId;
    }

}
