package ru.practicum.shareit.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.request.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    private User user1;
    private User user2;
    private Request request1;
    private Request request2;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        entityManager.persist(user2);

        // Создаем запросы
        request1 = new Request();
        request1.setDescription("Request 1");
        request1.setCreated(LocalDateTime.now().minusDays(1));
        request1.setRequester(user1);
        entityManager.persist(request1);

        request2 = new Request();
        request2.setDescription("Request 2");
        request2.setCreated(LocalDateTime.now());
        request2.setRequester(user2);
        entityManager.persist(request2);
    }

    @Test
    void testFindByRequesterIdOrderByCreatedDesc() {
        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).isEqualTo(request1);
    }

    @Test
    void testFindAllExcludingRequester() {
        List<Request> requests = requestRepository.findAllExcludingRequester(user1.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).isEqualTo(request2);
    }

    @Test
    void testFindById() {
        Optional<Request> foundRequest = requestRepository.findById(request1.getId());
        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get()).isEqualTo(request1);
    }
}