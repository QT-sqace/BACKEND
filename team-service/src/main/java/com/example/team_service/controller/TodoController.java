package com.example.team_service.controller;

import com.example.team_service.dto.response.TodoDTO;
import com.example.team_service.entity.Todo;
import com.example.team_service.service.TodoService;
import com.example.team_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final JwtUtil jwtUtil;

    // 팀 ID와 사용자 ID로 할 일 조회
    @GetMapping("/team")
    public ResponseEntity<List<Todo>> getTodosForTeam(@RequestParam Long teamId, @RequestParam Long userId) {
        return ResponseEntity.ok(todoService.getTodosForTeam(teamId, userId));
    }

    // 팀 ID로 할 일 조회
    @GetMapping
    public ResponseEntity<List<Todo>> getTodosByTeamId(@RequestParam Long teamId) {
        return ResponseEntity.ok(todoService.getTodosByTeamId(teamId));
    }

    // 할 일 생성
    @PostMapping
    public ResponseEntity<Todo> createTodo(
            @RequestParam Long teamId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TodoDTO request) {

        return ResponseEntity.ok(todoService.createTodo(teamId, authorizationHeader, request.getDescription()));
    }

    // 할 일 상태 업데이트
    @PutMapping("/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long todoId, @RequestParam Boolean completed) {
        return ResponseEntity.ok(todoService.updateTodoStatus(todoId, completed));
    }

    // 할 일 삭제
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Map<String, String>> deleteTodo(@PathVariable Long todoId) {
        Map<String, String> response = todoService.deleteTodoById(todoId);
        return ResponseEntity.ok(response); // 성공 메시지를 JSON 형식으로 반환
    }
}