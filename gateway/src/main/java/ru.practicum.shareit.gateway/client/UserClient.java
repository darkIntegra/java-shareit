package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.user.UserDto;

@Component
public class UserClient extends BaseClient {

    private static final String BASE_URL = "http://localhost:9090/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    // Добавление пользователя
    public ResponseEntity<Object> addUser(UserDto userDto) {
        return post(BASE_URL, null, null, userDto);
    }

    // Обновление пользователя
    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        String path = BASE_URL + "/" + userId;
        System.out.println("Sending PATCH request to URL: " + path);
        System.out.println("Request body: " + userDto);
        return patch(path, userId, null, userDto);
    }

    // Получение пользователя по ID
    public ResponseEntity<Object> getUserById(Long userId) {
        String path = BASE_URL + "/" + userId;
        return get(path, userId, null);
    }

    // Получение всех пользователей
    public ResponseEntity<Object> getAllUsers() {
        return get(BASE_URL, null, null);
    }

    // Удаление пользователя по ID
    public ResponseEntity<Object> deleteUserById(Long userId) {
        String path = BASE_URL + "/" + userId;
        return delete(path, userId, null);
    }

    // Удаление всех пользователей
    public ResponseEntity<Object> deleteAllUsers() {
        return delete(BASE_URL, null, null);
    }
}