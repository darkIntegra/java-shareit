package ru.practicum.shareit.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.user.UserDto;
import ru.practicum.shareit.gateway.validation.OnCreate;
import ru.practicum.shareit.gateway.validation.OnUpdate;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated(OnCreate.class) UserDto dto) {
        log.info("POST-запрос на создание пользователя: {}", dto);
        return userClient.addUser(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id, @RequestBody @Validated(OnUpdate.class) UserDto dto) {
        log.info("PATCH-запрос на обновление пользователя: id={}, dto={}", id, dto);
        return userClient.updateUser(id, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GET-запрос на получение пользователя: id={}", id);
        return userClient.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET-запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        log.info("DELETE-запрос на удаление пользователя: id={}", id);
        return userClient.deleteUserById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteAllUsers() {
        log.info("DELETE-запрос на удаление всех пользователей");
        return userClient.deleteAllUsers();
    }
}