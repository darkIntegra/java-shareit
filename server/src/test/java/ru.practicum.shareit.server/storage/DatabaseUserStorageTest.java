package ru.practicum.shareit.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import exception.ConflictException;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.storage.user.DatabaseUserStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(DatabaseUserStorage.class)
class DatabaseUserStorageTest {

    @Autowired
    private DatabaseUserStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        clearDatabase();

        // Создаем пользователя
        user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
    }

    private void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void testAddUser() {
        // Act
        User savedUser = userStorage.addUser(user);

        // Assert
        assertThat(savedUser).isNotNull();
        Long userId = savedUser.getId();
        assertThat(userId).isNotNull();
        Optional<User> foundUser = userStorage.findUserById(userId);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testAddUserWithDuplicateEmail() {
        // Arrange
        userStorage.addUser(user);

        // Act & Assert
        User duplicateUser = new User();
        duplicateUser.setName("Jane Doe");
        duplicateUser.setEmail("john.doe@example.com"); // Такой email уже существует

        assertThrows(ConflictException.class, () -> userStorage.addUser(duplicateUser));
    }

    @Test
    void testFindUserById() {
        // Arrange
        User savedUser = userStorage.addUser(user);
        Long userId = savedUser.getId();

        // Act
        Optional<User> foundUser = userStorage.findUserById(userId);

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        userStorage.addUser(user);

        // Act
        Collection<User> users = userStorage.getAllUsers();

        // Assert
        assertThat(users).hasSize(1);
        User firstUser = users.iterator().next();
        assertThat(firstUser.getName()).isEqualTo("John Doe");
        assertThat(firstUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User savedUser = userStorage.addUser(user);
        Long userId = savedUser.getId();

        // Создаем обновленного пользователя
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");

        // Act
        User savedUpdatedUser = userStorage.updateUser(userId, updatedUser);

        // Assert
        assertThat(savedUpdatedUser).isNotNull();
        assertThat(savedUpdatedUser.getId()).isEqualTo(userId);
        assertThat(savedUpdatedUser.getName()).isEqualTo("John Updated");
        assertThat(savedUpdatedUser.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        // Arrange
        User savedUser = userStorage.addUser(user);
        Long userId = savedUser.getId();

        // Добавляем второго пользователя
        User anotherUser = new User();
        anotherUser.setName("Jane Doe");
        anotherUser.setEmail("jane.doe@example.com");
        userStorage.addUser(anotherUser);

        // Act & Assert
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("jane.doe@example.com"); // Такой email уже существует

        assertThrows(ConflictException.class, () -> userStorage.updateUser(userId, updatedUser));
    }

    @Test
    void testDeleteUserById() {
        // Arrange
        User savedUser = userStorage.addUser(user);
        Long userId = savedUser.getId();

        // Act
        userStorage.deleteUserById(userId);

        // Assert
        Optional<User> deletedUser = userStorage.findUserById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testDeleteAllUsers() {
        // Arrange
        userStorage.addUser(user);

        // Act
        userStorage.deleteAllUsers();

        // Assert
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users).isEmpty();
    }

    @Test
    void testExistsById() {
        // Arrange
        User savedUser = userStorage.addUser(user);
        Long userId = savedUser.getId();

        // Act & Assert
        boolean exists = userStorage.existsById(userId);
        assertThat(exists).isTrue();
    }
}