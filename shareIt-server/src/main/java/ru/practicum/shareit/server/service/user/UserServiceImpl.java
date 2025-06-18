package ru.practicum.shareit.server.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.mapper.user.UserMapper;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto dto) {
        // Создаём пользователя
        User user = UserMapper.toUser(dto);
        User savedUser = userStorage.addUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        System.out.println("Обновление пользователя с ID: " + id);
        System.out.println("DTO запроса: " + dto);

        User existingUser = userStorage.findUserById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"));

        if (dto.getName() != null) {
            existingUser.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }

        System.out.println("Обновленная сущность пользователя: " + existingUser);

        User updatedUser = userStorage.updateUser(id, existingUser);
        if (updatedUser == null) {
            throw new IllegalStateException("Хранилище пользователей вернуло null после обновления");
        }

        System.out.println("Возвращаем DTO обновленного пользователя");
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userStorage.findUserById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }
}