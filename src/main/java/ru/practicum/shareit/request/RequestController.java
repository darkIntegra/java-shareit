package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // Создание нового запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody RequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    // Получение запроса по ID
    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }

    // Получение всех запросов пользователя
    @GetMapping
    public List<RequestDto> getAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequestsByUser(userId);
    }

    // Получение всех запросов других пользователей
    @GetMapping("/all")
    public List<RequestDto> getAllRequestsExcludingUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequestsExcludingUser(userId);
    }
}