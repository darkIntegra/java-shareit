package ru.practicum.shareit.user.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Repository
@Profile("!in-memory")
public class DatabaseUserStorage implements UserStorage {
    private final UserRepository userRepository;

    public DatabaseUserStorage(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        // Проверяем уникальность email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updateUser) {
        // Проверяем существование пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"));

        // Если email изменён, проверяем его уникальность
        if (!Objects.equals(updateUser.getEmail(), existingUser.getEmail())) {
            if (userRepository.existsByEmail(updateUser.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }

        // Обновляем данные пользователя
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());

        return userRepository.save(existingUser);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
