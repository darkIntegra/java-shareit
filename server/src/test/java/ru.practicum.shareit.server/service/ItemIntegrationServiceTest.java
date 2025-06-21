package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.repository.item.CommentRepository;
import ru.practicum.shareit.server.service.item.ItemService;
import ru.practicum.shareit.server.storage.item.ItemStorage;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/schema.sql"}) // Создает таблицы из schema.sql
class ItemIntegrationServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userStorage.addUser(owner);

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
        itemStorage.addItem(item);
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицы после каждого теста
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemStorage.deleteAllItems();
        userStorage.deleteAllUsers();
    }

    @Test
    void testAddItem_WithRealDatabase() {
        // Arrange
        Long userId = owner.getId();
        ItemDto dto = ItemDto.builder()
                .name("New Item")
                .description("Description")
                .available(true)
                .build();

        // Act
        ItemDto result = itemService.addItem(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
    }

    @Test
    void testUpdateItem_WithRealDatabase() {
        // Arrange
        Long userId = owner.getId();
        Long itemId = item.getId();
        ItemDto dto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        // Act
        ItemDto result = itemService.updateItem(userId, itemId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
    }

    @Test
    void testGetItemById_WithRealDatabase() {
        // Arrange
        Long itemId = item.getId();

        // Act
        ItemDto result = itemService.getItemById(itemId);

        // Assert
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void testSearchItems_WithRealDatabase() {
        // Arrange
        String text = "bike";

        // Act
        Collection<ItemDto> result = itemService.searchItems(text);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}