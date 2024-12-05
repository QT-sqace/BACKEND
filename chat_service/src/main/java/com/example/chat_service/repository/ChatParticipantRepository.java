package com.example.chat_service.repository;

import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    // 특정 사용자 ID로 참여 중인 채팅방 조회
    List<ChatParticipant> findByUserId(Long userId);

    // 특정 채팅방에 참여 중인 모든 사용자 조회
    List<ChatParticipant> findByRoom(ChatRoom room);

    // 특정 채팅방과 사용자 ID로 참여자 조회
    Optional<ChatParticipant> findByRoomAndUserId(ChatRoom room, Long senderId);

    @Query("SELECT p FROM ChatParticipant p WHERE p.room.roomId = :roomId")
    List<ChatParticipant> findByRoomId(@Param("roomId") Long roomId);

    List<ChatParticipant> findByRoom_TeamId(Long teamId);
}
