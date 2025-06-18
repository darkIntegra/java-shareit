package ru.practicum.shareit.server.storage.user;

import ru.practicum.shareit.server.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(Long id, User updateUser);

    Collection<User> getAllUsers();

    Optional<User> findUserById(Long id);

    void deleteUserById(Long id);

    void deleteAllUsers();

    boolean existsById(Long userId);
}
