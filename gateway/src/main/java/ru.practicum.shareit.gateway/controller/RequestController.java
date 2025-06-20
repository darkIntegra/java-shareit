package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.RequestClient;
import ru.practicum.shareit.gateway.dto.request.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestClient requestClient;

    // Создание нового запроса
    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestDto requestDto) {
        log.info("POST-запрос на создание запроса: {}, userId={}", requestDto, userId);
        return requestClient.createRequest(userId, requestDto);
    }

    // Получение всех запросов текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех запросов пользователя: userId={}", userId);
        return requestClient.getAllRequestsByUser(userId);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExcludingUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех запросов, кроме запросов пользователя: userId={}", userId);
        return requestClient.getAllRequestsExcludingUser(userId);
    }

    // Получение одного запроса по ID
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable Long requestId) {
        log.info("GET-запрос на получение запроса: requestId={}", requestId);
        return requestClient.getRequestById(requestId);
    }
}