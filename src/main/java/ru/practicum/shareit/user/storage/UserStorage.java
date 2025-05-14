package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(long id, User updateUser);

    Collection<User> getAllUsers();

    Optional<User> findUserById(long id);

    void deleteUserById(long id);

    void deleteAllUsers();
}
