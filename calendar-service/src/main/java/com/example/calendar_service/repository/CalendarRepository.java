package com.example.calendar_service.repository;

import com.example.calendar_service.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Calendar findByUserId(Long userId);
}
