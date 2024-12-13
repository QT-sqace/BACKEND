package com.example.team_service.service;

import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.external.BasicInfoDto;
import com.example.team_service.dto.external.TodoInfoDto;
import com.example.team_service.dto.external.UserInfoDto;
import com.example.team_service.dto.response.TodoDTO;
import com.example.team_service.entity.Team;
import com.example.team_service.entity.Todo;
import com.example.team_service.repository.TeamRepository;
import com.example.team_service.repository.TodoRepository;
import com.example.team_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class TodoService {

    private final UserServiceClient userServiceClient;
    private final TodoRepository todoRepository;
    private final TeamRepository teamRepository;
    private final JwtUtil jwtUtil;

    public List<TodoDTO> getTodosForTeam(Long teamId, Long userId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // 할 일 목록 조회
        List<Todo> todos = todoRepository.findAllByTeam_TeamIdAndUserId(teamId, userId);

        // 프로필 이미지 포함한 DTO 리스트 생성
        return todos.stream().map(todo -> {
            // 유저 프로필 정보 가져오기
            UserInfoDto userInfo = userServiceClient.getUserInfo(todo.getUserId());

            return new TodoDTO(
                    todo.getTodoId(),
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.getCompleted(),
                    todo.getUserName(),
                    userInfo.getProfileImage() // 프로필 이미지 추가
            );
        }).toList();
    }

    public Todo createTodo(Long teamId, String authorizationHeader, String title, String description) {
        Long userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);

        // 유저 정보 조회
        BasicInfoDto basicInfo = userServiceClient.getUserBasicInfo(userId);
        if (basicInfo == null || basicInfo.getUserName() == null) {
            throw new RuntimeException("User not found or invalid user data");
        }

        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // todo 생성
        Todo todo = new Todo(null, title, description, false, userId, basicInfo.getUserName(), team);

        // 저장
        return todoRepository.save(todo);
    }

    public List<TodoDTO> getTodosByTeamId(Long teamId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // 할 일 목록 조회
        List<Todo> todos = todoRepository.findAllByTeam_TeamId(teamId);

        // 프로필 이미지 포함한 DTO 리스트 생성
        return todos.stream().map(todo -> {
            // 유저 프로필 정보 가져오기
            UserInfoDto userInfo = userServiceClient.getUserInfo(todo.getUserId());

            return new TodoDTO(
                    todo.getTodoId(),
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.getCompleted(),
                    todo.getUserName(),
                    userInfo.getProfileImage() // 프로필 이미지 추가
            );
        }).toList();
    }

    // 완료 상태 수정
    public Todo updateTodoStatus(Long todoId, Boolean completed) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // 상태가 변경되지 않으면 예외 처리
        if (todo.getCompleted().equals(completed)) {
            throw new RuntimeException("Todo status is already " + completed);
        }

        todo.setCompleted(completed);
        return todoRepository.save(todo);
    }

    public TodoDTO updateTodoIfOwner(Long todoId, Long userId, TodoDTO todoDTO) {
        try {
            // Find the Todo
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));

            // Check ownership
            if (!todo.getUserId().equals(userId)) {
                throw new RuntimeException("Only the owner can update this Todo");
            }

            // Update fields except `completed`
            todo.setTitle(todoDTO.getTitle());
            todo.setDescription(todoDTO.getDescription());

            // Save and fetch updated data
            Todo updatedTodo = todoRepository.save(todo);
            String userProfile = userServiceClient.getUserProfile(updatedTodo.getUserId());

            return new TodoDTO(
                    updatedTodo.getTodoId(),
                    updatedTodo.getTitle(),
                    updatedTodo.getDescription(),
                    updatedTodo.getCompleted(),
                    updatedTodo.getUserName(),
                    userProfile
            );
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating Todo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage(), e);
        }
    }


    public Map<String, String> deleteTodoById(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // 단순 삭제 처리
        todoRepository.delete(todo);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Todo successfully deleted.");
        response.put("status", "success");
        return response;
    }
}