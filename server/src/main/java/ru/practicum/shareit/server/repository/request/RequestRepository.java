package ru.practicum.shareit.server.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.model.request.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    // Получить все запросы конкретного пользователя (сортировка по дате создания)
    List<Request> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    // Получить все запросы, кроме тех, что созданы конкретным пользователем (сортировка по дате создания)
    @Query("SELECT r FROM Request r WHERE r.requester.id <> :userId ORDER BY r.created DESC")
    List<Request> findAllExcludingRequester(@Param("userId") Long userId);

    // Получить запрос по ID
    Optional<Request> findById(Long requestId);
}