package com.example.team_service.controller;

import com.example.team_service.dto.response.TodoDTO;
import com.example.team_service.entity.Todo;
import com.example.team_service.service.TodoService;
import com.example.team_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final JwtUtil jwtUtil;

    // 팀 ID와 유저 ID로 할 일 조회
    @GetMapping("/team/{teamId}/user")
    public ResponseEntity<List<TodoDTO>> getTodosForTeam(
            @PathVariable Long teamId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);
            List<TodoDTO> todos = todoService.getTodosForTeam(teamId, userId);
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch todos", e);
        }
    }

    // 팀 ID로 할 일 조회
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TodoDTO>> getTodosByTeamId(@PathVariable Long teamId) {
        try {
            List<TodoDTO> todos = todoService.getTodosByTeamId(teamId);
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch todos for team", e);
        }
    }

    // 할 일 생성
    @PostMapping
    public ResponseEntity<Todo> createTodo(
            @RequestParam Long teamId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TodoDTO request) {

        return ResponseEntity.ok(todoService.createTodo(teamId, authorizationHeader, request.getTitle(), request.getDescription()));
    }

    // 할 일 상태 업데이트
    @PutMapping("/{todoId}/status")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long todoId, @RequestParam Boolean completed) {
        return ResponseEntity.ok(todoService.updateTodoStatus(todoId, completed));
    }

    // 할 일 수정
    @PutMapping("/{todoId}/update")
    public ResponseEntity<TodoDTO> updateTodo(
            @PathVariable Long todoId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TodoDTO todoDTO) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);
            TodoDTO updatedTodo = todoService.updateTodoIfOwner(todoId, userId, todoDTO);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update todo", e);
        }
    }

    // 할 일 삭제
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Map<String, String>> deleteTodo(@PathVariable Long todoId) {
        Map<String, String> response = todoService.deleteTodoById(todoId);
        return ResponseEntity.ok(response); // 성공 메시지를 JSON 형식으로 반환
    }
}