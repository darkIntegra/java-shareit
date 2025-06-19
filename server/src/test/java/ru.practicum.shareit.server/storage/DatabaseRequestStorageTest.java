package ru.practicum.shareit.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.request.RequestRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.storage.request.DatabaseRequestStorage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Import(DatabaseRequestStorage.class)
class DatabaseRequestStorageTest {

    @Autowired
    private DatabaseRequestStorage requestStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User requester;
    private Request request;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных
        clearDatabase();

        // Создаем пользователя (запросчик)
        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        userRepository.save(requester);

        // Создаем запрос
        request = new Request();
        request.setDescription("Request for a bike");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
    }

    private void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM requests");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void testAddRequest() {
        // Act
        Request savedRequest = requestStorage.addRequest(request);

        // Assert
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getRequester().getId()).isEqualTo(requester.getId());
        assertThat(savedRequest.getCreated()).isNotNull(); // Проверяем, что время создания установлено
    }

    @Test
    void testFindRequestById() {
        // Arrange
        Request savedRequest = requestStorage.addRequest(request);
        Long requestId = savedRequest.getId();

        // Act
        Optional<Request> foundRequest = requestStorage.findRequestById(requestId);

        // Assert
        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getId()).isEqualTo(requestId);
        assertThat(foundRequest.get().getDescription()).isEqualTo("Request for a bike");
        assertThat(foundRequest.get().getRequester().getId()).isEqualTo(requester.getId());
        assertThat(foundRequest.get().getCreated()).isNotNull(); // Проверяем, что время создания установлено
    }

    @Test
    void testGetAllRequests() {
        // Arrange
        requestStorage.addRequest(request);

        // Act
        Collection<Request> requests = requestStorage.getAllRequests();

        // Assert
        assertThat(requests).hasSize(1);
        Request firstRequest = requests.iterator().next();
        assertThat(firstRequest.getDescription()).isEqualTo("Request for a bike");
        assertThat(firstRequest.getRequester().getId()).isEqualTo(requester.getId());
        assertThat(firstRequest.getCreated()).isNotNull(); // Проверяем, что время создания установлено
    }

    @Test
    void testUpdateRequest() {
        // Arrange
        Request savedRequest = requestStorage.addRequest(request);
        Long requestId = savedRequest.getId();

        // Создаем обновленный запрос
        Request updatedRequest = new Request();
        updatedRequest.setDescription("Updated request for a bike");
        updatedRequest.setRequester(requester);

        // Act
        Request savedUpdatedRequest = requestStorage.updateRequest(requestId, updatedRequest);

        // Assert
        assertThat(savedUpdatedRequest).isNotNull();
        assertThat(savedUpdatedRequest.getId()).isEqualTo(requestId);
        assertThat(savedUpdatedRequest.getDescription()).isEqualTo("Updated request for a bike");
        assertThat(savedUpdatedRequest.getRequester().getId()).isEqualTo(requester.getId());
        assertThat(savedUpdatedRequest.getCreated()).isNotNull(); // Проверяем, что время создания установлено
    }

    @Test
    void testDeleteRequestById() {
        // Arrange
        Request savedRequest = requestStorage.addRequest(request);
        Long requestId = savedRequest.getId();

        // Act
        requestStorage.deleteRequestById(requestId);

        // Assert
        Optional<Request> deletedRequest = requestStorage.findRequestById(requestId);
        assertThat(deletedRequest).isEmpty();
    }

    @Test
    void testDeleteAllRequests() {
        // Arrange
        requestStorage.addRequest(request);

        // Act
        requestStorage.deleteAllRequests();

        // Assert
        Collection<Request> requests = requestStorage.getAllRequests();
        assertThat(requests).isEmpty();
    }

    @Test
    void testGetRequestsByRequestorId() {
        // Arrange
        requestStorage.addRequest(request);

        // Act
        Collection<Request> requests = requestStorage.getRequestsByRequestorId(requester.getId());

        // Assert
        assertThat(requests).hasSize(1);
        Request firstRequest = requests.iterator().next();
        assertThat(firstRequest.getDescription()).isEqualTo("Request for a bike");
        assertThat(firstRequest.getRequester().getId()).isEqualTo(requester.getId());
        assertThat(firstRequest.getCreated()).isNotNull(); // Проверяем, что время создания установлено
    }
}