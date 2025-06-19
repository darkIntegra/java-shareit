package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.dto.request.RequestDto;

import java.util.Map;

@Service

public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    // Создание нового запроса
    public ResponseEntity<Object> createRequest(Long userId, RequestDto requestDto) {
        return post("", userId, null, requestDto);
    }

    // Получение всех запросов текущего пользователя
    public ResponseEntity<Object> getAllRequestsByUser(Long userId) {
        Map<String, Object> parameters = Map.of("userId", userId);
        return get("", userId, parameters);
    }

    // Получение всех запросов, кроме тех, что созданы текущим пользователем
    public ResponseEntity<Object> getAllRequestsExcludingUser(Long userId) {
        String path = "/all";
        Map<String, Object> parameters = Map.of("userId", userId);
        return get(path, userId, parameters);
    }

    // Получение одного запроса по ID
    public ResponseEntity<Object> getRequestById(Long requestId) {
        String path = "/" + requestId;
        return get(path, null, null);
    }
}