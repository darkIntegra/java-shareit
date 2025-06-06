package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDto createRequest(Long userId, RequestDto requestDto) {
        // Создаем новый запрос
        Request request = RequestMapper.toRequest(requestDto);
        request.setRequestor(User.builder().id(userId).build());
        request.setCreated(LocalDateTime.now());

        // Сохраняем запрос в базу данных
        Request savedRequest = requestRepository.save(request);

        // Возвращаем DTO созданного запроса
        return RequestMapper.toRequestDto(savedRequest);
    }

    @Override
    public RequestDto getRequestById(Long requestId) {
        // Находим запрос по ID
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос с ID=" + requestId + " не найден"));

        // Преобразуем в DTO
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getAllRequestsByUser(Long userId) {
        // Находим все запросы пользователя
        return requestRepository.findByRequestorId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequestsExcludingUser(Long userId) {
        // Находим все запросы, кроме тех, что созданы указанным пользователем
        return requestRepository.findByRequestorIdNot(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}