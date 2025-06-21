package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.storage.user.UserStorage;
import ru.practicum.shareit.server.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserStorage userStorage;

    private User user;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    void testAddUser() {
        // Arrange
        UserDto dto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userStorage.addUser(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Устанавливаем ID для сохраненного пользователя
            return savedUser;
        });

        // Act
        UserDto addedUser = userService.addUser(dto);

        // Assert
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getName()).isEqualTo(dto.getName());
        assertThat(addedUser.getEmail()).isEqualTo(dto.getEmail());
        verify(userStorage, times(1)).addUser(any(User.class));
    }

    @Test
    void testUpdateUser() {
        // Arrange
        UserDto dto = UserDto.builder()
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        // Создаем существующего пользователя
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");

        // Настройка мока для findUserById
        when(userStorage.findUserById(1L)).thenReturn(Optional.of(existingUser));

        // Настройка мока для updateUser
        when(userStorage.updateUser(eq(1L), any(User.class))).thenAnswer(invocation -> {
            User updatedUser = invocation.getArgument(1); // Второй аргумент (обновленный пользователь)
            return updatedUser;
        });

        // Act
        UserDto updatedUser = userService.updateUser(1L, dto);

        // Assert
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(dto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(dto.getEmail());

        verify(userStorage, times(1)).findUserById(1L);
        verify(userStorage, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userStorage.findUserById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDto userDto = userService.getUserById(1L);

        // Assert
        assertThat(userDto).isNotNull();
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        verify(userStorage, times(1)).findUserById(1L);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        when(userStorage.getAllUsers()).thenReturn(List.of(user));

        // Act
        List<UserDto> users = (List<UserDto>) userService.getAllUsers();

        // Assert
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(user.getName());
        assertThat(users.get(0).getEmail()).isEqualTo(user.getEmail());
        verify(userStorage, times(1)).getAllUsers();
    }

    @Test
    void testDeleteUserById() {
        // Act
        userService.deleteUserById(1L);

        // Assert
        verify(userStorage, times(1)).deleteUserById(1L);
    }

    @Test
    void testDeleteAllUsers() {
        // Act
        userService.deleteAllUsers();

        // Assert
        verify(userStorage, times(1)).deleteAllUsers();
    }
}