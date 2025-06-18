package ru.practicum.shareit.server.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
