package com.example.calendar_service.repository;

import com.example.calendar_service.entity.CalendarShared;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarSharedRepository extends JpaRepository<CalendarShared, Long> {
    List<CalendarShared> findByCalendarId(Long calendarId);

    void deleteByCalendarIdAndEventId(Long calendarId,Long eventId);

    // 팀 일정 삭제 시 해당 eventId에 연결된 모든 데이터 삭제
    @Modifying
    @Query("DELETE FROM CalendarShared cs WHERE cs.eventId = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);

    //특정 개인 캘린더와 이벤트 ID로 동기화 여부 확인
    boolean existsByCalendarIdAndEventId(Long calendarId, Long eventId);
}
