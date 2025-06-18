package ru.practicum.shareit.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // Создание нового запроса
    @PostMapping
    public ResponseEntity<RequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestDto requestDto) {
        RequestDto createdRequest = requestService.createRequest(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    // Получение всех запросов текущего пользователя
    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<RequestDto> requests = requestService.getAllRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getAllRequestsExcludingUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<RequestDto> requests = requestService.getAllRequestsExcludingUser(userId);
        return ResponseEntity.ok(requests);
    }

    // Получение одного запроса по ID
    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable Long requestId) {
        RequestDto request = requestService.getRequestById(requestId);
        return ResponseEntity.ok(request);
    }
}