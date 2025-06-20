package ru.practicum.shareit.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    // Создание нового запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestDto requestDto) {
        log.info("POST-запрос на создание запроса: {}, userId={}", requestDto, userId);
        return requestService.createRequest(userId, requestDto);
    }

    // Получение всех запросов текущего пользователя
    @GetMapping
    public List<RequestDto> getAllRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех запросов пользователя: userId={}", userId);
        return requestService.getAllRequestsByUser(userId);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    @GetMapping("/all")
    public List<RequestDto> getAllRequestsExcludingUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех запросов, кроме запросов пользователя: userId={}", userId);
        return requestService.getAllRequestsExcludingUser(userId);
    }

    // Получение одного запроса по ID
    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId) {
        log.info("GET-запрос на получение запроса: requestId={}", requestId);
        return requestService.getRequestById(requestId);
    }
}