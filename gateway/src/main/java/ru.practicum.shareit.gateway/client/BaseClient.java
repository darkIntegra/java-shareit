package ru.practicum.shareit.gateway.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    // GET request
    protected ResponseEntity<Object> get(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    // POST request
    protected <T> ResponseEntity<Object> post(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    // PUT request
    protected <T> ResponseEntity<Object> put(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    // PATCH request
    protected <T> ResponseEntity<Object> patch(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    // DELETE request
    protected ResponseEntity<Object> delete(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    // Общий метод для отправки запросов
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        System.out.println("Отправляем PATCH-запрос:");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);
        System.out.println("Parameters: " + parameters);
        System.out.println("Body: " + body);

        try {
            ResponseEntity<Object> response = rest.exchange(
                    path,
                    method,
                    requestEntity,
                    Object.class,
                    parameters != null ? parameters : new HashMap<>()
            );
            System.out.println("Получен ответ от сервера: " + response.getStatusCode());
            return response;
        } catch (HttpStatusCodeException e) {
            System.out.println("Ошибка при отправке запроса: " + e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    // Формирование заголовков
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    // Подготовка ответа
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        return Optional.ofNullable(response)
                .filter(r -> r.getStatusCode().is2xxSuccessful())
                .orElseGet(() -> {
                    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
                    return response.hasBody() ? responseBuilder.body(response.getBody()) : responseBuilder.build();
                });
    }
}