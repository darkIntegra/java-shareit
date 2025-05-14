package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;

    //напрямую внедряю через конструктор (можно было через @Autowired) но мне так надежнее
    public UserController(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto dto) {
        User user = UserMapper.toUser(dto);
        User savedUser = inMemoryUserStorage.addUser(user);
        return UserMapper.toUserDto(savedUser);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody @Valid UserDto dto) {
        User user = UserMapper.toUser(dto);
        User updateUser = inMemoryUserStorage.updateUser(id, user);
        return UserMapper.toUserDto(updateUser);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users = new ArrayList<>(inMemoryUserStorage.getAllUsers());
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}
