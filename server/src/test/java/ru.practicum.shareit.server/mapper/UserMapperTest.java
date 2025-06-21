package ru.practicum.shareit.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.mapper.user.UserMapper;
import ru.practicum.shareit.server.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void testToUser() {
        // Arrange
        UserDto dto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        // Act
        User user = UserMapper.toUser(dto);

        // Assert
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void testToUserDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Act
        UserDto dto = UserMapper.toUserDto(user);

        // Assert
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }
}