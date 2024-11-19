package com.example.calendar_service.repository;

import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.entity.CalendarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CalendarInfoRepository extends JpaRepository<CalendarInfo, Long> {
    List<CalendarInfo> findByCalendarAndEventType(Calendar calendar, CalendarInfo.EventType eventType);
}
