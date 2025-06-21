package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.model.item.Comment;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.repository.item.CommentRepository;
import ru.practicum.shareit.server.service.item.ItemService;
import ru.practicum.shareit.server.storage.item.ItemStorage;
import ru.practicum.shareit.server.storage.request.RequestStorage;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemStorage itemStorage;

    @MockBean
    private RequestStorage requestStorage;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        // Создаем вещь
        item = new Item();
        item.setId(1L);
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    void addItem_ShouldThrowException_WhenRequestIdIsInvalid() {
        Long userId = 1L;
        ItemDto dto = ItemDto.builder()
                .name("New Item")
                .description("Description of new item")
                .available(true)
                .requestId(999L) // Несуществующий requestId
                .build();

        when(userStorage.findUserById(userId)).thenReturn(Optional.of(owner));
        when(requestStorage.findRequestById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.addItem(userId, dto));
        verify(requestStorage, times(1)).findRequestById(999L);
    }

    @Test
    void addItem_ShouldAddItem_WhenRequestIdIsValid() {
        Long userId = 1L;
        ItemDto dto = ItemDto.builder()
                .name("New Item")
                .description("Description of new item")
                .available(true)
                .requestId(1L) // Существующий requestId
                .build();

        Request request = new Request();
        request.setId(1L);

        when(userStorage.findUserById(userId)).thenReturn(Optional.of(owner));
        when(requestStorage.findRequestById(1L)).thenReturn(Optional.of(request));
        when(itemStorage.addItem(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(2L); // Устанавливаем ID для сохраненной вещи
            return savedItem;
        });

        ItemDto addedItem = itemService.addItem(userId, dto);

        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo(dto.getName());
        assertThat(addedItem.getRequestId()).isEqualTo(dto.getRequestId());
        verify(itemStorage, times(1)).addItem(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowException_WhenUserIsNotOwner() {
        Long userId = 2L; // Другой пользователь
        Long itemId = 1L;
        ItemDto dto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(userId, itemId, dto));
        verify(itemStorage, times(1)).findItemById(itemId);
    }

    @Test
    void searchItems_ShouldReturnMatchingItems_WhenTextIsProvided() {
        String text = "bike";
        Item matchingItem = new Item();
        matchingItem.setId(1L);
        matchingItem.setName("Bike");
        matchingItem.setDescription("Mountain bike for rent");
        matchingItem.setAvailable(true);

        when(itemStorage.getAllItems()).thenReturn(List.of(matchingItem));

        Collection<ItemDto> result = itemService.searchItems(text);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Bike", result.iterator().next().getName());
    }

    @Test
    void addComment_ShouldThrowException_WhenUserHasNoBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        when(userStorage.findUserById(userId)).thenReturn(Optional.of(owner));
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByUserAndItemAndApprovedStatus(userId, itemId)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, commentDto));
    }

    @Test
    void getItemById_ShouldReturnItemWithComments() {
        // Arrange
        Long itemId = 1L;

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());

        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));

        // Act
        ItemDto result = itemService.getItemById(itemId);

        // Assert
        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertFalse(result.getComments().isEmpty());
        assertEquals("Great item!", result.getComments().get(0).getText());
    }

    @Test
    void getAllItems_ShouldReturnEmptyList_WhenUserHasNoItems() {
        // Arrange
        Long userId = 1L;

        when(itemStorage.getItemsByOwnerId(userId)).thenReturn(Collections.emptyList());

        // Act
        Collection<ItemDto> result = itemService.getAllItems(userId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteItemById_ShouldDeleteItem() {
        // Arrange
        Long itemId = 1L;

        doNothing().when(itemStorage).deleteItemById(itemId);

        // Act
        itemService.deleteItemById(itemId);

        // Assert
        verify(itemStorage, times(1)).deleteItemById(itemId);
    }

    @Test
    void deleteAllItems_ShouldDeleteAllItems() {
        // Arrange
        doNothing().when(itemStorage).deleteAllItems();

        // Act
        itemService.deleteAllItems();

        // Assert
        verify(itemStorage, times(1)).deleteAllItems();
    }

    @Test
    void addComment_ShouldThrowException_WhenBookingIsNotCompleted() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        when(userStorage.findUserById(userId)).thenReturn(Optional.of(owner));
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByUserAndItemAndApprovedStatus(userId, itemId)).thenReturn(true);
        when(bookingRepository.existsByUserAndItemAndApprovedStatusAndEndDateBefore(userId, itemId, LocalDateTime.now()))
                .thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> itemService.addComment(userId, itemId, commentDto));
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsEmpty() {
        // Arrange
        String text = "";

        // Act
        Collection<ItemDto> result = itemService.searchItems(text);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void updateItem_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto dto = ItemDto.builder()
                .name("Updated Name")
                .build();

        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.updateItem(eq(itemId), any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        // Act
        ItemDto updatedItem = itemService.updateItem(userId, itemId, dto);

        // Assert
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription()); // Не должно измениться
        assertEquals(item.getAvailable(), updatedItem.getAvailable()); // Не должно измениться
    }


}