package com.example.calendar_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calendar_info")
@Getter
@NoArgsConstructor
public class CalendarInfo {
    //일정 등록 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(nullable = false)
    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private Boolean allDay;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    private String color;

    public enum EventType {
        PERSONAL,
        TEAM
    }

    //처음 일정 등록시 사용
    public CalendarInfo(Calendar calendar, String title, String content, String startDate, String endDate,
                        Boolean allDay, String color, EventType eventType) {
        this.calendar = calendar;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allDay = allDay;
        this.color = color;
        this.eventType = eventType;
    }
}
