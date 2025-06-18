package ru.practicum.shareit.server.storage.user;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import exception.ConflictException;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.user.UserRepository;

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
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        // Находим существующего пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + id + " не найден"));

        // Если email изменён, проверяем его уникальность
        if (!Objects.equals(updatedUser.getEmail(), existingUser.getEmail())) {
            userRepository.findByEmail(updatedUser.getEmail())
                    .ifPresent(foundUser -> {
                        throw new ConflictException("Пользователь с таким email уже существует");
                    });
        }

        // Обновляем данные пользователя
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

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