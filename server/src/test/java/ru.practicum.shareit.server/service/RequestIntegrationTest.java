package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.request.RequestRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.service.request.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/schema.sql"}) // Создает таблицы из schema.sql
class RequestIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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
        userRepository.save(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        // Создаем запросы
        request1 = new Request();
        request1.setDescription("Request 1");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        requestRepository.save(request1);

        request2 = new Request();
        request2.setDescription("Request 2");
        request2.setRequester(user2);
        request2.setCreated(LocalDateTime.now());
        requestRepository.save(request2);

        // Создаем вещи, связанные с запросами
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequestId(request1.getId());
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(user2);
        item2.setRequestId(request2.getId());
        itemRepository.save(item2);
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицы после каждого теста
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateRequest() {
        // Arrange
        Long userId = user1.getId();
        RequestDto requestDto = RequestDto.builder()
                .description("New Request")
                .build();

        // Act
        RequestDto result = requestService.createRequest(userId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Request", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void testGetAllRequestsByUser() {
        // Arrange
        Long userId = user1.getId();

        // Act
        List<RequestDto> result = requestService.getAllRequestsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request 1", result.get(0).getDescription());
    }

    @Test
    void testGetAllRequestsExcludingUser() {
        // Arrange
        Long userId = user1.getId();

        // Act
        List<RequestDto> result = requestService.getAllRequestsExcludingUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request 2", result.get(0).getDescription());
    }

    @Test
    void testGetRequestById() {
        // Arrange
        Long requestId = request1.getId();

        // Act
        RequestDto result = requestService.getRequestById(requestId);

        // Assert
        assertNotNull(result);
        assertEquals("Request 1", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Item 1", result.getItems().get(0).getName());
    }

    @Test
    void testGetRequestById_ThrowsException_WhenRequestNotFound() {
        // Arrange
        Long nonExistentRequestId = 999L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(nonExistentRequestId));
    }
}