package com.example.team_service.service;

import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.external.BasicInfoDto;
import com.example.team_service.dto.external.TodoInfoDto;
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

    public List<Todo> getTodosForTeam(Long teamId, Long userId) {
        return todoRepository.findAllByTeam_TeamIdAndUserId(teamId, userId);
    }

    public Todo createTodo(Long teamId, String authorizationHeader, String title, String description) {
        Long userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);

        // 유저 정보 조회
        BasicInfoDto basicInfo = userServiceClient.getUserBasicInfo(userId);
        if (basicInfo == null || basicInfo.getUserName() == null) {
            throw new RuntimeException("User not found or invalid user data");
        }

        // TodoInfoDto 생성
        TodoInfoDto todoInfo = new TodoInfoDto(userId, basicInfo.getUserName());

        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // todo 생성
        Todo todo = new Todo(null, title, description, false, userId, basicInfo.getUserName(), team);

        // 저장
        return todoRepository.save(todo);
    }

    public List<Todo> getTodosByTeamId(Long teamId) {
        return todoRepository.findAllByTeam_TeamId(teamId);
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

    // todo 수정
    public Todo updateTodo(Long todoId, String title, String description) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todo.setTitle(title);
        todo.setDescription(description);
        return todoRepository.save(todo);
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