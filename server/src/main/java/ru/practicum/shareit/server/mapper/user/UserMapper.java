package ru.practicum.shareit.server.mapper.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.model.user.User;

@Component
public class UserMapper {

    public static User toUser(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

