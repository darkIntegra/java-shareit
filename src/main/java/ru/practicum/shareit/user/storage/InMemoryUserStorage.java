package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    //Несмотря на проверки в контроллере, использую принцип защиты слоев, также убрал из DTO id. Id присваивает сервер
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public User addUser(User user) {
        Objects.requireNonNull(user, "Новый пользователь не может быть null");
        user.setUserId(nextId.getAndIncrement());
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User updateUser(long id, User updateUser) {
        Objects.requireNonNull(updateUser, "Пользователь для обновления не может быть null");
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        users.put(id, updateUser);
        return updateUser;
    }

    //добавил защиту от прямого изменения извне
    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с ID=" + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
