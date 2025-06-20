package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.service.booking.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/schema.sql"})
class BookingIntegrationServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        // Создаем арендатора
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker);

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        // Создаем бронирование
        Booking booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().minusDays(1));
        booking1.setEndDate(LocalDateTime.now().plusDays(1));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().plusDays(1));
        booking2.setEndDate(LocalDateTime.now().plusDays(3));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицы после каждого теста
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testAddBooking_Success() {
        // Arrange
        Long bookerId = 2L;
        Long itemId = 1L;

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(2)) // Начало через 2 дня
                .end(LocalDateTime.now().plusDays(3))   // Конец через 3 дня
                .build();

        // Act
        BookingDto result = bookingService.addBooking(bookerId, createDto);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals(createDto.getStart(), result.getStart());
        assertEquals(createDto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void testAddBooking_ThrowsException_WhenItemIsNotAvailable() {
        // Arrange
        Long userId = 2L;
        Long itemId = 1L;

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        item.setAvailable(false);
        itemRepository.save(item);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(userId, createDto));
    }

    @Test
    void testAddBooking_ThrowsException_WhenBookingOverlaps() {
        // Arrange
        Long userId = 2L;
        Long itemId = 1L;

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(userId, createDto));
    }

    @Test
    void testGetBookingById_ThrowsException_WhenBookingNotFound() {
        // Arrange
        Long nonExistentBookingId = 999L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(nonExistentBookingId));
    }

    @Test
    void testGetAllBookingsByUser_Success() {
        // Arrange
        Long userId = 2L;
        BookingState state = BookingState.ALL;

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsByUser(userId, state, userId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBookingsByUser_ThrowsException_WhenUserHasNoAccess() {
        // Arrange
        Long userId = 1L;
        Long requesterId = 2L; // Не тот же пользователь
        BookingState state = BookingState.ALL;

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> bookingService.getAllBookingsByUser(userId, state, requesterId));
    }

    @Test
    void testGetAllBookingsForOwnerItems_WithRealDatabase() {
        // Arrange
        Long ownerId = 1L;

        // Act
        var result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }
}