package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.request.RequestDto;

import java.util.Map;

@Component
public class RequestClient extends BaseClient {

    private static final String BASE_URL = "http://localhost:9090/requests";

    public RequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    // Создание нового запроса
    public ResponseEntity<Object> createRequest(Long userId, RequestDto requestDto) {
        return post(BASE_URL, userId, null, requestDto);
    }

    // Получение всех запросов текущего пользователя
    public ResponseEntity<Object> getAllRequestsByUser(Long userId) {
        String path = BASE_URL;
        Map<String, Object> parameters = Map.of("userId", userId);
        return get(path, userId, parameters);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    public ResponseEntity<Object> getAllRequestsExcludingUser(Long userId) {
        String path = BASE_URL + "/all";
        Map<String, Object> parameters = Map.of("userId", userId);
        return get(path, userId, parameters);
    }

    // Получение одного запроса по ID
    public ResponseEntity<Object> getRequestById(Long requestId) {
        String path = BASE_URL + "/" + requestId;
        return get(path, null, null);
    }
}