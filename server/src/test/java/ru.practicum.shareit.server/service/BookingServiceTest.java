package ru.practicum.shareit.server.service;

import exception.ForbiddenException;
import exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.service.booking.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        // Создаем арендатора
        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        // Создаем вещь
        item = new Item();
        item.setId(1L);
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);

        // Создаем бронирование
        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item); // Связываем бронирование с вещью
        booking.setBooker(booker); // Устанавливаем пользователя
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStartDate(LocalDateTime.now().minusHours(1)); // Начало за час до текущего времени
        booking.setEndDate(LocalDateTime.now().plusHours(1)); // Конец через час после текущего времени
    }

    @Test
    void testAddBooking() {
        // Arrange
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        // Создаём мок для сохранения бронирования
        Booking savedBooking = new Booking();
        savedBooking.setId(1L); // Устанавливаем ID для сохранённого объекта
        savedBooking.setStartDate(createDto.getStart());
        savedBooking.setEndDate(createDto.getEnd());
        savedBooking.setItem(item);
        savedBooking.setBooker(booker);
        savedBooking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Act
        BookingDto bookingDto = bookingService.addBooking(booker.getId(), createDto);

        // Assert
        assertNotNull(bookingDto);
        assertEquals(item.getId(), bookingDto.getItemId());
        assertEquals(createDto.getStart(), bookingDto.getStart());
        assertEquals(createDto.getEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void addBooking_ShouldThrowException_WhenItemIsNotAvailable() {
        // Arrange
        Long userId = 1L;
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        item.setAvailable(false); // Делаем вещь недоступной

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(userId, createDto));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void addBooking_ShouldThrowException_WhenBookingOverlaps() {
        // Arrange
        Long userId = 1L;
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndDateOverlap(1L, createDto.getStart(), createDto.getEnd()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(userId, createDto));
        verify(bookingRepository, times(1))
                .existsByItemIdAndDateOverlap(1L, createDto.getStart(), createDto.getEnd());
    }

    @Test
    void getAllBookingsByUser_ShouldThrowException_WhenUserHasNoAccess() {
        // Arrange
        Long userId = 1L;
        Long requesterId = 2L; // Не тот же пользователь
        BookingState state = BookingState.ALL;

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> bookingService.getAllBookingsByUser(userId, state, requesterId));
    }

    @Test
    void getAllBookingsByUser_ShouldReturnCurrentBookings() {
        // Arrange
        Long userId = 2L; // Пользователь, который сделал бронирование
        BookingState state = BookingState.CURRENT;

        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsByUser(userId, state, userId);

        // Assert
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "Result should contain exactly one booking");
        verify(bookingRepository, times(1)).findByBookerId(userId);
    }

    @Test
    void testUpdateBooking_Approved() {
        // Arrange
        Booking existingBooking = new Booking();
        existingBooking.setId(1L);
        existingBooking.setItem(item);
        existingBooking.setBooker(booker);
        existingBooking.setStatus(BookingStatus.WAITING);
        existingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        existingBooking.setEndDate(LocalDateTime.now().plusHours(2));

        when(bookingRepository.findById(existingBooking.getId())).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingDto updatedBooking = bookingService.updateBooking(owner.getId(), existingBooking.getId(), true);

        // Assert
        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testUpdateBooking_Rejected() {
        // Arrange
        Booking existingBooking = new Booking();
        existingBooking.setId(1L);
        existingBooking.setItem(item);
        existingBooking.setBooker(booker);
        existingBooking.setStatus(BookingStatus.WAITING);
        existingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        existingBooking.setEndDate(LocalDateTime.now().plusHours(2));

        when(bookingRepository.findById(existingBooking.getId())).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingDto updatedBooking = bookingService
                .updateBooking(owner.getId(), existingBooking.getId(), false);

        // Assert
        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void testUpdateBooking_ThrowsException_WhenBookingNotFound() {
        // Arrange
        Long nonExistentBookingId = 999L;

        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> bookingService
                .updateBooking(owner.getId(), nonExistentBookingId, true));
    }

    @Test
    void testUpdateBooking_ThrowsException_WhenUserIsNotOwner() {
        // Arrange
        Booking existingBooking = new Booking();
        existingBooking.setId(1L);
        existingBooking.setItem(item);
        existingBooking.setBooker(booker);
        existingBooking.setStatus(BookingStatus.WAITING);
        existingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        existingBooking.setEndDate(LocalDateTime.now().plusHours(2));

        when(bookingRepository.findById(existingBooking.getId())).thenReturn(Optional.of(existingBooking));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> bookingService
                .updateBooking(booker.getId(), existingBooking.getId(), true));
    }

    @Test
    void testGetBookingById_Success() {
        // Arrange
        Long bookingId = 1L;
        Booking existingBooking = new Booking();
        existingBooking.setId(bookingId);
        existingBooking.setItem(item);
        existingBooking.setBooker(booker);
        existingBooking.setStatus(BookingStatus.APPROVED);
        existingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        existingBooking.setEndDate(LocalDateTime.now().plusHours(2));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        // Act
        BookingDto result = bookingService.getBookingById(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(existingBooking.getStatus(), result.getStatus());
        assertEquals(existingBooking.getStartDate(), result.getStart());
        assertEquals(existingBooking.getEndDate(), result.getEnd());
    }

    @Test
    void testGetBookingById_ThrowsException_WhenBookingNotFound() {
        // Arrange
        Long nonExistentBookingId = 999L;

        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(nonExistentBookingId));
    }

    @Test
    void testGetAllBookingsForOwnerItems_Success() {
        // Arrange
        Long ownerId = owner.getId();
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStartDate(LocalDateTime.now().minusHours(1));
        booking1.setEndDate(LocalDateTime.now().plusHours(1));

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStartDate(LocalDateTime.now().plusHours(1));
        booking2.setEndDate(LocalDateTime.now().plusHours(2));

        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(bookings);

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking1.getStartDate(), result.get(0).getStart());
        assertEquals(booking1.getEndDate(), result.get(0).getEnd());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking2.getStartDate(), result.get(1).getStart());
        assertEquals(booking2.getEndDate(), result.get(1).getEnd());
    }

    @Test
    void testGetAllBookingsForOwnerItems_ThrowsException_WhenOwnerNotFound() {
        // Arrange
        Long nonExistentOwnerId = 999L;

        when(userRepository.existsById(nonExistentOwnerId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingsForOwnerItems(nonExistentOwnerId, BookingState.ALL));
    }

    @Test
    void testGetAllBookingsForOwnerItems_FilteredByState_Current() {
        // Arrange
        Long ownerId = owner.getId();
        Booking currentBooking = new Booking();
        currentBooking.setId(1L);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStartDate(LocalDateTime.now().minusHours(1));
        currentBooking.setEndDate(LocalDateTime.now().plusHours(1));

        Booking futureBooking = new Booking();
        futureBooking.setId(2L);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStartDate(LocalDateTime.now().plusHours(1));
        futureBooking.setEndDate(LocalDateTime.now().plusHours(2));

        List<Booking> bookings = List.of(currentBooking, futureBooking);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(bookings);

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.CURRENT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getStartDate(), result.get(0).getStart());
        assertEquals(currentBooking.getEndDate(), result.get(0).getEnd());
    }

    @Test
    void testGetAllBookingsForOwnerItems_ReturnsEmptyList() {
        // Arrange
        Long ownerId = owner.getId();

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(Collections.emptyList());

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterBookingsByState_All_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking booking1 = createBooking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), BookingStatus.APPROVED);
        Booking booking2 = createBooking(2L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.REJECTED);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(booking1, booking2));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.ALL);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFilterBookingsByState_Current_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking currentBooking = createBooking(1L, LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), BookingStatus.APPROVED);
        Booking pastBooking = createBooking(2L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.REJECTED);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(currentBooking, pastBooking));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.CURRENT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void testFilterBookingsByState_Past_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking pastBooking = createBooking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.REJECTED);
        Booking futureBooking = createBooking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(pastBooking, futureBooking));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.PAST);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void testFilterBookingsByState_Future_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking futureBooking = createBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking currentBooking = createBooking(2L, LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), BookingStatus.APPROVED);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(futureBooking, currentBooking));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.FUTURE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void testFilterBookingsByState_Waiting_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking waitingBooking = createBooking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking approvedBooking = createBooking(2L, LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), BookingStatus.APPROVED);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(waitingBooking, approvedBooking));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.WAITING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
    }

    @Test
    void testFilterBookingsByState_Rejected_ThroughPublicMethod() {
        // Arrange
        Long ownerId = owner.getId();
        Booking rejectedBooking = createBooking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.REJECTED);
        Booking waitingBooking = createBooking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(rejectedBooking, waitingBooking));

        // Act
        List<BookingShortDto> result = bookingService.getAllBookingsForOwnerItems(ownerId, BookingState.REJECTED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }

    // Вспомогательный метод для создания объектов Booking
    private Booking createBooking(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStartDate(start);
        booking.setEndDate(end);
        return booking;
    }
}