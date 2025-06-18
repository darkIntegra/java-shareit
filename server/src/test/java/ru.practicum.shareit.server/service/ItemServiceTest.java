package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Comment;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.repository.item.CommentRepository;
import ru.practicum.shareit.server.service.item.ItemService;
import ru.practicum.shareit.server.storage.item.ItemStorage;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemStorage itemStorage;

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
    void testAddItem() {
        // Arrange
        ItemDto dto = ItemDto.builder()
                .name("New Item")
                .description("Description of new item")
                .available(true)
                .build();

        when(userStorage.findUserById(1L)).thenReturn(Optional.of(owner));
        when(itemStorage.addItem(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(2L); // Устанавливаем ID для сохраненной вещи
            return savedItem;
        });

        // Act
        ItemDto addedItem = itemService.addItem(1L, dto);

        // Assert
        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo(dto.getName());
        assertThat(addedItem.getDescription()).isEqualTo(dto.getDescription());
        assertThat(addedItem.getAvailable()).isEqualTo(dto.getAvailable());

        verify(itemStorage, times(1)).addItem(any(Item.class));
    }

    @Test
    void testUpdateItem() {
        // Arrange
        ItemDto dto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        // Создаем объект Item для мока
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old Name");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        // Настройка мока
        when(itemStorage.findItemById(1L)).thenReturn(Optional.of(existingItem));
        when(itemStorage.updateItem(eq(1L), any(Item.class))).thenAnswer(invocation -> {
            Item updatedItem = invocation.getArgument(1); // Второй аргумент (обновленная вещь)
            return updatedItem;
        });

        // Act
        ItemDto updatedItem = itemService.updateItem(1L, 1L, dto);

        // Assert
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo(dto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(dto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(dto.getAvailable());

        verify(itemStorage, times(1)).findItemById(1L);
        verify(itemStorage, times(1)).updateItem(eq(1L), any(Item.class));
    }

    @Test
    void testGetItemById() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());

        when(itemStorage.findItemById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(List.of(comment));

        // Act
        ItemDto itemDto = itemService.getItemById(1L);

        // Assert
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Great item!");

        verify(itemStorage, times(1)).findItemById(1L);
    }

    @Test
    void testAddComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().minusDays(10)); // Начало бронирования 10 дней назад
        booking.setEndDate(LocalDateTime.now().minusDays(5));    // Окончание бронирования 5 дней назад
        booking.setStatus(BookingStatus.APPROVED);

        // Фиксируем текущее время
        LocalDateTime now = LocalDateTime.of(2025, 6, 17, 13, 23, 8);

        // Мок для LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(now);

            when(userStorage.findUserById(1L)).thenReturn(Optional.of(owner));
            when(itemStorage.findItemById(1L)).thenReturn(Optional.of(item));
            when(bookingRepository.existsByUserAndItemAndApprovedStatus(1L, 1L)).thenReturn(true);

            // Настройка мока для existsByUserAndItemAndApprovedStatusAndEndDateBefore
            when(bookingRepository.existsByUserAndItemAndApprovedStatusAndEndDateBefore(
                    eq(1L), eq(1L), eq(now)
            )).thenReturn(true);

            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment savedComment = invocation.getArgument(0);
                savedComment.setId(1L);
                return savedComment;
            });

            // Act
            CommentDto addedComment = itemService.addComment(1L, 1L, commentDto);

            // Assert
            assertThat(addedComment).isNotNull();
            assertThat(addedComment.getText()).isEqualTo(commentDto.getText());

            verify(commentRepository, times(1)).save(any(Comment.class));
        }
    }
}