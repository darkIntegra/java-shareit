package ru.practicum.shareit.server.service.request;

import ru.practicum.shareit.server.dto.request.RequestDto;

import java.util.List;

public interface RequestService {

    // Создать новый запрос
    RequestDto createRequest(Long userId, RequestDto requestDto);

    // Получить все запросы конкретного пользователя
    List<RequestDto> getAllRequestsByUser(Long userId);

    // Получить все запросы, кроме тех, что созданы конкретным пользователем
    List<RequestDto> getAllRequestsExcludingUser(Long userId);

    // Получить один запрос по ID
    RequestDto getRequestById(Long requestId);
}