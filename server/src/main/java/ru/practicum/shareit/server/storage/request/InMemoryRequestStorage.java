package ru.practicum.shareit.server.storage.request;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.model.request.Request;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("in-memory")
public class InMemoryRequestStorage implements RequestStorage {

    private final Map<Long, Request> requests = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Request addRequest(Request request) {
        // Устанавливаем ID для нового запроса
        request.setId(nextId.getAndIncrement());
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Request updateRequest(Long requestId, Request updatedRequest) {
        // Проверяем существование запроса
        if (!requests.containsKey(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        // Обновляем данные запроса
        updatedRequest.setId(requestId);
        requests.put(requestId, updatedRequest);
        return updatedRequest;
    }

    @Override
    public Collection<Request> getAllRequests() {
        // Возвращаем все запросы
        return Collections.unmodifiableCollection(requests.values());
    }

    @Override
    public Optional<Request> findRequestById(Long requestId) {
        // Ищем запрос по ID
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public Collection<Request> getRequestsByRequestorId(Long requestorId) {
        // Фильтруем запросы по ID пользователя-запросчика
        return requests.values().stream()
                .filter(request -> Objects.equals(request.getRequester().getId(), requestorId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRequestById(Long requestId) {
        // Проверяем существование запроса перед удалением
        if (!requests.containsKey(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        requests.remove(requestId);
    }

    @Override
    public void deleteAllRequests() {
        // Очищаем хранилище
        requests.clear();
    }

    @Override
    public boolean existsById(Long requestId) {
        // Проверяем наличие запроса по ID
        return requests.containsKey(requestId);
    }
}