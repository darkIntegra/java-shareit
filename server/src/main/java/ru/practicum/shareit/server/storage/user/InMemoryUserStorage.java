package ru.practicum.shareit.server.storage.user;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.exception.ConflictException;
import ru.practicum.shareit.server.model.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("in-memory")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final Set<String> emails = new HashSet<>();

    @Override
    public User addUser(User user) {
        // Проверяем уникальность email
        if (!emails.add(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User updateUser) {
        User existingUser = users.get(id);
        if (existingUser == null) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        // Если email изменён, проверяем его уникальность
        if (!Objects.equals(updateUser.getEmail(), existingUser.getEmail())) {
            if (!emails.add(updateUser.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            emails.remove(existingUser.getEmail()); // Удаляем старый email
        }
        updateUser.setId(id);
        users.put(id, updateUser);
        return updateUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        emails.remove(user.getEmail()); // Удаляем email из множества
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        emails.clear(); // Очищаем множество email
    }

    @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }
}