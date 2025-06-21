package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.dto.user.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    // Добавление пользователя
    public ResponseEntity<Object> addUser(UserDto userDto) {
        return post("", null, null, userDto);
    }

    // Обновление пользователя
    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        String path = "/" + userId;
        return patch(path, userId, null, userDto);
    }

    // Получение пользователя по ID
    public ResponseEntity<Object> getUserById(Long userId) {
        String path = "/" + userId;
        return get(path, userId, null);
    }

    // Получение всех пользователей
    public ResponseEntity<Object> getAllUsers() {
        return get("", null, null);
    }

    // Удаление пользователя по ID
    public ResponseEntity<Object> deleteUserById(Long userId) {
        String path = "/" + userId;
        return delete(path, userId, null);
    }

    // Удаление всех пользователей
    public ResponseEntity<Object> deleteAllUsers() {
        return delete("", null, null);
    }
}