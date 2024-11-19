package com.example.calendar_service.service;

import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamCalendarService {

    private final CalendarRepository calendarRepository;

    public void createCalendarForTeam(Long teamId) {
        Calendar calendar = new Calendar(teamId, true);
        calendarRepository.save(calendar);
    }
}
