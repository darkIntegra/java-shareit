//package ru.practicum.shareit.server.storage;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import ru.practicum.shareit.server.model.item.Item;
//import ru.practicum.shareit.server.model.user.User;
//import ru.practicum.shareit.server.storage.item.ItemStorage;
//
//import java.util.Collection;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("in-memory")
//class InMemoryItemStorageTest {
//
//    @Autowired
//    private ItemStorage itemStorage;
//
//    private User owner;
//    private Item item;
//
//    @BeforeEach
//    void setUp() {
//        // Очищаем хранилище
//        itemStorage.deleteAllItems();
//
//        // Создаем пользователя (владельца вещи)
//        owner = new User();
//        owner.setId(1L);
//        owner.setName("Owner");
//        owner.setEmail("owner@example.com");
//
//        // Создаем вещь
//        item = new Item();
//        item.setName("Bike");
//        item.setDescription("Mountain bike for rent");
//        item.setAvailable(true);
//        item.setOwner(owner);
//    }
//
//    @Test
//    void testAddItem() {
//        // Act
//        Item savedItem = itemStorage.addItem(item);
//
//        // Assert
//        assertThat(savedItem).isNotNull();
//        Long itemId = savedItem.getId();
//        assertThat(itemId).isNotNull();
//        assertThat(itemStorage.findItemById(itemId)).isPresent();
//    }
//
//    @Test
//    void testFindItemById() {
//        // Arrange
//        Item savedItem = itemStorage.addItem(item);
//        Long itemId = savedItem.getId();
//
//        // Act
//        Optional<Item> foundItem = itemStorage.findItemById(itemId);
//
//        // Assert
//        assertThat(foundItem).isPresent();
//        assertThat(foundItem.get().getId()).isEqualTo(itemId);
//        assertThat(foundItem.get().getName()).isEqualTo("Bike");
//        assertThat(foundItem.get().getOwner().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    void testGetAllItems() {
//        // Arrange
//        itemStorage.addItem(item);
//
//        // Act
//        Collection<Item> items = itemStorage.getAllItems();
//
//        // Assert
//        assertThat(items).hasSize(1);
//        Item firstItem = items.iterator().next();
//        assertThat(firstItem.getName()).isEqualTo("Bike");
//        assertThat(firstItem.getOwner().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    void testUpdateItem() {
//        // Arrange
//        Item savedItem = itemStorage.addItem(item);
//        Long itemId = savedItem.getId();
//
//        // Создаем обновленную вещь
//        Item updatedItem = new Item();
//        updatedItem.setName("Updated Bike");
//        updatedItem.setDescription("Updated description");
//        updatedItem.setAvailable(false);
//        updatedItem.setOwner(owner);
//
//        // Act
//        Item savedUpdatedItem = itemStorage.updateItem(itemId, updatedItem);
//
//        // Assert
//        assertThat(savedUpdatedItem).isNotNull();
//        assertThat(savedUpdatedItem.getId()).isEqualTo(itemId);
//        assertThat(savedUpdatedItem.getName()).isEqualTo("Updated Bike");
//        assertThat(savedUpdatedItem.getDescription()).isEqualTo("Updated description");
//        assertThat(savedUpdatedItem.getAvailable()).isFalse();
//    }
//
//    @Test
//    void testDeleteItemById() {
//        // Arrange
//        Item savedItem = itemStorage.addItem(item);
//        Long itemId = savedItem.getId();
//
//        // Act
//        itemStorage.deleteItemById(itemId);
//
//        // Assert
//        Optional<Item> deletedItem = itemStorage.findItemById(itemId);
//        assertThat(deletedItem).isEmpty();
//    }
//
//    @Test
//    void testDeleteAllItems() {
//        // Arrange
//        itemStorage.addItem(item);
//
//        // Act
//        itemStorage.deleteAllItems();
//
//        // Assert
//        Collection<Item> items = itemStorage.getAllItems();
//        assertThat(items).isEmpty();
//    }
//
//    @Test
//    void testGetItemsByOwnerId() {
//        // Arrange
//        itemStorage.addItem(item);
//
//        // Act
//        Collection<Item> items = itemStorage.getItemsByOwnerId(1L);
//
//        // Assert
//        assertThat(items).hasSize(1);
//        Item firstItem = items.iterator().next();
//        assertThat(firstItem.getName()).isEqualTo("Bike");
//        assertThat(firstItem.getOwner().getId()).isEqualTo(1L);
//    }
//}