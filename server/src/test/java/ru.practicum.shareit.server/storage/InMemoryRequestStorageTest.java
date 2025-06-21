//package ru.practicum.shareit.server.storage;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import ru.practicum.shareit.server.model.request.Request;
//import ru.practicum.shareit.server.model.user.User;
//import ru.practicum.shareit.server.storage.request.InMemoryRequestStorage;
//
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("in-memory")
//class InMemoryRequestStorageTest {
//
//    @Autowired
//    private InMemoryRequestStorage requestStorage;
//
//    private User requester;
//    private Request request;
//
//    @BeforeEach
//    void setUp() {
//        // Очищаем хранилище
//        requestStorage.deleteAllRequests();
//
//        // Создаем пользователя (запросчик)
//        requester = new User();
//        requester.setId(1L);
//        requester.setName("Requester");
//        requester.setEmail("requester@example.com");
//
//        // Создаем запрос
//        request = new Request();
//        request.setDescription("Request for a bike");
//        request.setCreated(LocalDateTime.now());
//        request.setRequester(requester);
//    }
//
//    @Test
//    void testAddRequest() {
//        // Act
//        Request savedRequest = requestStorage.addRequest(request);
//
//        // Assert
//        assertThat(savedRequest).isNotNull();
//        Long requestId = savedRequest.getId();
//        assertThat(requestId).isNotNull();
//        assertThat(requestStorage.findRequestById(requestId)).isPresent();
//    }
//
//    @Test
//    void testFindRequestById() {
//        // Arrange
//        Request savedRequest = requestStorage.addRequest(request);
//        Long requestId = savedRequest.getId();
//
//        // Act
//        Optional<Request> foundRequest = requestStorage.findRequestById(requestId);
//
//        // Assert
//        assertThat(foundRequest).isPresent();
//        assertThat(foundRequest.get().getId()).isEqualTo(requestId);
//        assertThat(foundRequest.get().getDescription()).isEqualTo("Request for a bike");
//        assertThat(foundRequest.get().getRequester().getId()).isEqualTo(1L);
//        assertThat(foundRequest.get().getCreated()).isNotNull();
//    }
//
//    @Test
//    void testGetAllRequests() {
//        // Arrange
//        requestStorage.addRequest(request);
//
//        // Act
//        Collection<Request> requests = requestStorage.getAllRequests();
//
//        // Assert
//        assertThat(requests).hasSize(1);
//        Request firstRequest = requests.iterator().next();
//        assertThat(firstRequest.getDescription()).isEqualTo("Request for a bike");
//        assertThat(firstRequest.getRequester().getId()).isEqualTo(1L);
//        assertThat(firstRequest.getCreated()).isNotNull();
//    }
//
//    @Test
//    void testUpdateRequest() {
//        // Arrange
//        Request savedRequest = requestStorage.addRequest(request);
//        Long requestId = savedRequest.getId();
//
//        // Создаем обновленный запрос
//        Request updatedRequest = new Request();
//        updatedRequest.setDescription("Updated request for a bike");
//        updatedRequest.setCreated(LocalDateTime.now());
//        updatedRequest.setRequester(requester);
//
//        // Act
//        Request savedUpdatedRequest = requestStorage.updateRequest(requestId, updatedRequest);
//
//        // Assert
//        assertThat(savedUpdatedRequest).isNotNull();
//        assertThat(savedUpdatedRequest.getId()).isEqualTo(requestId);
//        assertThat(savedUpdatedRequest.getDescription()).isEqualTo("Updated request for a bike");
//        assertThat(savedUpdatedRequest.getRequester().getId()).isEqualTo(1L);
//        assertThat(savedUpdatedRequest.getCreated()).isNotNull();
//    }
//
//    @Test
//    void testDeleteRequestById() {
//        // Arrange
//        Request savedRequest = requestStorage.addRequest(request);
//        Long requestId = savedRequest.getId();
//
//        // Act
//        requestStorage.deleteRequestById(requestId);
//
//        // Assert
//        Optional<Request> deletedRequest = requestStorage.findRequestById(requestId);
//        assertThat(deletedRequest).isEmpty();
//    }
//
//    @Test
//    void testDeleteAllRequests() {
//        // Arrange
//        requestStorage.addRequest(request);
//
//        // Act
//        requestStorage.deleteAllRequests();
//
//        // Assert
//        Collection<Request> requests = requestStorage.getAllRequests();
//        assertThat(requests).isEmpty();
//    }
//
//    @Test
//    void testGetRequestsByRequestorId() {
//        // Arrange
//        requestStorage.addRequest(request);
//
//        // Act
//        Collection<Request> requests = requestStorage.getRequestsByRequestorId(1L);
//
//        // Assert
//        assertThat(requests).hasSize(1);
//        Request firstRequest = requests.iterator().next();
//        assertThat(firstRequest.getDescription()).isEqualTo("Request for a bike");
//        assertThat(firstRequest.getRequester().getId()).isEqualTo(1L);
//        assertThat(firstRequest.getCreated()).isNotNull();
//    }
//}