package com.example.calendar_service.repository;

import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.entity.CalendarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalendarInfoRepository extends JpaRepository<CalendarInfo, Long> {
    List<CalendarInfo> findByCalendarAndEventType(Calendar calendar, CalendarInfo.EventType eventType);


    @Query("SELECT c FROM CalendarInfo c WHERE c.startDate >= :start AND c.startDate < :end")
    List<CalendarInfo> findEventsBetween(@Param("start") String start, @Param("end") String end);

}
