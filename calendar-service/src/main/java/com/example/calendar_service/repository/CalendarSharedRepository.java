package com.example.calendar_service.repository;

import com.example.calendar_service.entity.CalendarShared;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CalendarSharedRepository extends JpaRepository<CalendarShared, Long> {
    List<CalendarShared> findByCalendarId(Long calendarId);

    void deleteByCalendarIdAndEventId(Long eventId,Long calendarId);
}
