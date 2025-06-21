package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.service.user.UserService;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/schema.sql"})// Создает таблицы из schema.sql
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userStorage.addUser(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userStorage.addUser(user2);
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицы после каждого теста
        userStorage.deleteAllUsers();
    }

    @Test
    void testAddUser() {
        // Arrange
        UserDto dto = UserDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();

        // Act
        UserDto result = userService.addUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("New User", result.getName());
        assertEquals("newuser@example.com", result.getEmail());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        Long userId = user1.getId();
        UserDto dto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        // Act
        UserDto result = userService.updateUser(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void testGetUserById() {
        // Arrange
        Long userId = user1.getId();

        // Act
        UserDto result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(user1.getName(), result.getName());
        assertEquals(user1.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserById_ThrowsException_WhenUserNotFound() {
        // Arrange
        Long nonExistentUserId = 999L;

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.getUserById(nonExistentUserId));
    }

    @Test
    void testGetAllUsers() {
        // Act
        Collection<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteUserById() {
        // Arrange
        Long userId = user1.getId();

        // Act
        userService.deleteUserById(userId);

        // Assert
        assertThrows(NoSuchElementException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testDeleteAllUsers() {
        // Act
        userService.deleteAllUsers();

        // Assert
        assertTrue(userService.getAllUsers().isEmpty());
    }
}