package ru.practicum.shareit.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.storage.item.DatabaseItemStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Import(DatabaseItemStorage.class)
class DatabaseItemStorageTest {

    @Autowired
    private DatabaseItemStorage itemStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных
        clearDatabase();

        // Создаем пользователя (владельца вещи)
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner); // Сохраняем владельца

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    private void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM items");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void testAddItem() {
        // Act
        Item savedItem = itemStorage.addItem(item);

        // Assert
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void testFindItemById() {
        // Arrange
        Item savedItem = itemStorage.addItem(item);
        Long itemId = savedItem.getId();

        // Act
        Optional<Item> foundItem = itemStorage.findItemById(itemId);

        // Assert
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getId()).isEqualTo(itemId);
        assertThat(foundItem.get().getName()).isEqualTo("Bike");
        assertThat(foundItem.get().getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void testGetAllItems() {
        // Arrange
        itemStorage.addItem(item);

        // Act
        Collection<Item> items = itemStorage.getAllItems();

        // Assert
        assertThat(items).hasSize(1);
        Item firstItem = items.iterator().next();
        assertThat(firstItem.getName()).isEqualTo("Bike");
        assertThat(firstItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void testUpdateItem() {
        // Arrange
        Item savedItem = itemStorage.addItem(item);
        Long itemId = savedItem.getId();

        // Создаем обновленную вещь
        Item updatedItem = new Item();
        updatedItem.setName("Updated Bike");
        updatedItem.setDescription("Updated description");
        updatedItem.setAvailable(false);
        updatedItem.setOwner(owner);

        // Act
        Item savedUpdatedItem = itemStorage.updateItem(itemId, updatedItem);

        // Assert
        assertThat(savedUpdatedItem).isNotNull();
        assertThat(savedUpdatedItem.getId()).isEqualTo(itemId);
        assertThat(savedUpdatedItem.getName()).isEqualTo("Updated Bike");
        assertThat(savedUpdatedItem.getDescription()).isEqualTo("Updated description");
        assertThat(savedUpdatedItem.getAvailable()).isFalse();
    }

    @Test
    void testDeleteItemById() {
        // Arrange
        Item savedItem = itemStorage.addItem(item);
        Long itemId = savedItem.getId();

        // Act
        itemStorage.deleteItemById(itemId);

        // Assert
        Optional<Item> deletedItem = itemStorage.findItemById(itemId);
        assertThat(deletedItem).isEmpty();
    }

    @Test
    void testDeleteAllItems() {
        // Arrange
        itemStorage.addItem(item);

        // Act
        itemStorage.deleteAllItems();

        // Assert
        Collection<Item> items = itemStorage.getAllItems();
        assertThat(items).isEmpty();
    }

    @Test
    void testGetItemsByOwnerId() {
        // Arrange
        itemStorage.addItem(item);

        // Act
        Collection<Item> items = itemStorage.getItemsByOwnerId(owner.getId());

        // Assert
        assertThat(items).hasSize(1);
        Item firstItem = items.iterator().next();
        assertThat(firstItem.getName()).isEqualTo("Bike");
        assertThat(firstItem.getOwner().getId()).isEqualTo(owner.getId());
    }
}