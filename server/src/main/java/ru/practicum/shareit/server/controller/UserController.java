package ru.practicum.shareit.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.service.user.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // Создание нового пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto dto) {
        log.info("POST-запрос на создание пользователя: {}", dto);

        UserDto createdUser = userService.addUser(dto);

        log.info("Создан пользователь: {}", createdUser);

        return createdUser;
    }

    // Обновление пользователя
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        log.info("PATCH-запрос для ID {}: {}", id, dto);

        dto.setId(id);

        UserDto updatedUser = userService.updateUser(id, dto);

        log.info("Обновлен пользователь: {}", updatedUser);

        return updatedUser;
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("GET-запрос пользователя по ID: {}", id);

        return userService.getUserById(id);
    }

    // Получение всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("GET-запрос на получение всех пользователей");

        return userService.getAllUsers();
    }

    // Удаление пользователя по ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        log.info("DELETE-запрос на удаление пользователя по ID: {}", id);

        userService.deleteUserById(id);
    }

    // Удаление всех пользователей
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllUsers() {
        log.info("DELETE-запрос на удаление всех пользователей");

        userService.deleteAllUsers();
    }
}