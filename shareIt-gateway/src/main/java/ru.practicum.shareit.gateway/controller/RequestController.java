package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.RequestClient;
import ru.practicum.shareit.gateway.dto.request.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    // Создание нового запроса
    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestDto requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }

    // Получение всех запросов текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequestsByUser(userId);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExcludingUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequestsExcludingUser(userId);
    }

    // Получение одного запроса по ID
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable Long requestId) {
        return requestClient.getRequestById(requestId);
    }
}