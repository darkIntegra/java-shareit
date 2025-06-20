package ru.practicum.shareit.server.service.request;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.mapper.request.RequestMapper;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.repository.request.RequestRepository;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, RequestDto requestDto) {
        // Проверяем существование пользователя
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        // Создаем сущность Request из DTO
        Request request = RequestMapper.toRequest(requestDto);
        request.setRequester(requester); // Устанавливаем автора запроса
        request.setCreated(LocalDateTime.now()); // Устанавливаем текущее время создания

        // Сохраняем запрос в базу данных
        Request savedRequest = requestRepository.save(request);

        // Возвращаем DTO созданного запроса (без связанных вещей, так как они еще не созданы)
        return RequestMapper.toRequestDto(savedRequest);
    }

    // Получить все запросы конкретного пользователя
    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequestsByUser(Long userId) {
        // Находим все запросы пользователя
        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        // Преобразуем в DTO
        return requests.stream()
                .map(this::convertToDtoWithItems) // Преобразуем каждый запрос в DTO
                .collect(Collectors.toList());
    }

    // Получить все запросы, кроме тех, что созданы конкретным пользователем
    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequestsExcludingUser(Long userId) {
        // Находим все запросы, кроме тех, что созданы пользователем
        List<Request> requests = requestRepository.findAllExcludingRequester(userId);

        // Преобразуем в DTO
        return requests.stream()
                .map(this::convertToDtoWithItems) // Преобразуем каждый запрос в DTO
                .collect(Collectors.toList());
    }

    // Получить один запрос по ID
    @Override
    @Transactional(readOnly = true)
    public RequestDto getRequestById(Long requestId) {
        // Находим запрос
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID=" + requestId + " не найден"));

        // Преобразуем в DTO
        return convertToDtoWithItems(request);
    }

    // Вспомогательный метод для преобразования запроса в DTO с учетом связанных вещей
    private RequestDto convertToDtoWithItems(Request request) {
        // Находим все вещи, связанные с запросом
        List<Item> items = itemRepository.findByRequestId(request.getId());

        // Преобразуем запрос и связанные вещи в DTO
        return RequestMapper.toRequestDto(request, items);
    }

}