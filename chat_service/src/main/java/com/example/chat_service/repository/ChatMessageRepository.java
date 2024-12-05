package com.example.chat_service.repository;

import com.example.chat_service.entity.ChatMessage;
import com.example.chat_service.entity.ChatRoom;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // roomId로 가장 큰 roomMessageId 가져오기
    @Query("SELECT MAX(m.roomMessageId) FROM ChatMessage m WHERE m.room.roomId = :roomId")
    Optional<Long> findMaxRoomMessageIdByRoomId(@Param("roomId") Long roomId);

    // 가장 최근 메시지 가져오기
    ChatMessage findTopByRoomOrderBySendTimeDesc(ChatRoom room);

    // 가장 최근 50개 메시지 가져오기
    List<ChatMessage> findTop50ByRoomRoomIdOrderBySendTimeDesc(Long roomId);
}


