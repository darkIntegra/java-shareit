package ru.practicum.shareit.server.mapper.request;

import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.dto.item.RequestItemDto;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.model.request.Request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    // Преобразование сущности Request в DTO (без связанных вещей)
    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(Collections.emptyList()) // Пустой список вещей
                .build();
    }

    // Преобразование сущности Request в DTO (с учетом связанных вещей)
    public static RequestDto toRequestDto(Request request, List<Item> items) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items.stream()
                        .map(RequestMapper::toItemDto) // Преобразуем каждую вещь в ItemDto
                        .collect(Collectors.toList()))
                .build();
    }

    // Преобразование DTO в сущность Request
    public static Request toRequest(RequestDto requestDto) {
        return Request.builder()
                .description(requestDto.getDescription())
                .created(LocalDateTime.now()) // Устанавливаем текущее время
                .build();
    }

    // Преобразование сущности Item в RequestItemDto
    public static RequestItemDto toRequestItemDto(Item item, LocalDateTime created) {
        return RequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId()) // Получаем ownerId из связанного пользователя
                .created(created)
                .build();
    }

    // Преобразование сущности Item в ItemDto
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId()) // ID запроса, на который отвечает вещь
                .build();
    }
}