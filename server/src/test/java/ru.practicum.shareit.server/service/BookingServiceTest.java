package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.service.booking.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(BookingServiceImpl.class) // Импортируем тестируемый сервис
class BookingServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        // Создаем арендатора
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);
    }

    @Test
    @Transactional
    void testAddBooking() {
        // Arrange
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        // Act
        BookingDto bookingDto = bookingService.addBooking(booker.getId(), createDto);

        // Assert
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItemId()).isEqualTo(item.getId());
        assertThat(bookingDto.getStart()).isEqualTo(createDto.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(createDto.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    @Transactional
    void testUpdateBooking() {
        // Arrange
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        // Act
        BookingDto updatedBooking = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        // Assert
        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    @Transactional
    void testGetBookingById() {
        // Arrange
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        // Act
        BookingDto bookingDto = bookingService.getBookingById(booking.getId());

        // Assert
        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
    }

    @Test
    @Transactional
    void testGetAllBookingsByUser() {
        // Arrange
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().minusHours(2));
        booking.setEndDate(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking);

        // Act
        List<BookingShortDto> bookings = bookingService.getAllBookingsByUser(booker.getId(),
                BookingState.PAST, booker.getId());

        // Assert
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    @Transactional
    void testGetAllBookingsForOwnerItems() {
        // Arrange
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().minusHours(2));
        booking.setEndDate(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking);

        // Act
        List<BookingShortDto> bookings = bookingService.getAllBookingsForOwnerItems(owner.getId(), BookingState.PAST);

        // Assert
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    @Transactional
    void testGetAllBookings() {
        // Arrange
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusHours(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        // Act
        List<BookingDto> bookings = bookingService.getAllBookings(BookingState.ALL, booker.getId());

        // Assert
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    @Transactional
    void testAddBooking_Overlap() {
        // Arrange
        Booking existingBooking = new Booking();
        existingBooking.setStartDate(LocalDateTime.now().plusHours(1));
        existingBooking.setEndDate(LocalDateTime.now().plusHours(2));
        existingBooking.setItem(item);
        existingBooking.setBooker(booker);
        existingBooking.setStatus(BookingStatus.APPROVED);
        entityManager.persist(existingBooking);

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(booker.getId(), createDto));
    }
}