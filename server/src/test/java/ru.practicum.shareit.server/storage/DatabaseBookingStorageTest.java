package ru.practicum.shareit.server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.storage.booking.DatabaseBookingStorage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Import(DatabaseBookingStorage.class)
class DatabaseBookingStorageTest {

    @Autowired
    private DatabaseBookingStorage bookingStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных
        clearDatabase();

        // Создаем пользователя (владельца вещи)
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner); // Сохраняем владельца

        // Создаем пользователя (арендатора)
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        userRepository.save(booker); // Сохраняем арендатора

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item); // Сохраняем вещь

        // Создаем бронирование
        booking = new Booking();
        booking.setStartDate(LocalDateTime.now().minusDays(5));
        booking.setEndDate(LocalDateTime.now().minusDays(3));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
    }

    private void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM bookings");
        jdbcTemplate.execute("DELETE FROM items");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void testAddBooking() {
        // Act
        Booking savedBooking = bookingStorage.addBooking(booking);

        // Assert
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testFindBookingById() {
        // Arrange
        Booking savedBooking = bookingStorage.addBooking(booking); // Сохраняем бронирование
        Long bookingId = savedBooking.getId(); // Получаем реальный ID

        // Act
        Optional<Booking> foundBooking = bookingStorage.findBookingById(bookingId);

        // Assert
        assertThat(foundBooking).isPresent(); // Проверяем, что объект найден
        assertThat(foundBooking.get().getId()).isEqualTo(bookingId);
        assertThat(foundBooking.get().getBooker().getId()).isEqualTo(booker.getId());
        assertThat(foundBooking.get().getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testGetAllBookings() {
        // Arrange
        bookingStorage.addBooking(booking);

        // Act
        Collection<Booking> bookings = bookingStorage.getAllBookings();

        // Assert
        assertThat(bookings).hasSize(1);
        Booking firstBooking = bookings.iterator().next();
        assertThat(firstBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(firstBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testUpdateBooking() {
        // Arrange
        Booking savedBooking = bookingStorage.addBooking(booking); // Сохраняем бронирование
        Long bookingId = savedBooking.getId(); // Получаем реальный ID

        // Загружаем существующее бронирование из базы данных
        Booking existingBooking = bookingStorage.findBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Обновляем поля существующего бронирования
        existingBooking.setStartDate(LocalDateTime.now().minusDays(4));
        existingBooking.setEndDate(LocalDateTime.now().minusDays(2));
        existingBooking.setStatus(BookingStatus.REJECTED);

        // Act
        Booking savedUpdatedBooking = bookingStorage.updateBooking(bookingId, existingBooking);

        // Assert
        assertThat(savedUpdatedBooking).isNotNull();
        assertThat(savedUpdatedBooking.getId()).isEqualTo(bookingId);
        assertThat(savedUpdatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(savedUpdatedBooking.getStartDate()).isEqualTo(existingBooking.getStartDate());
        assertThat(savedUpdatedBooking.getEndDate()).isEqualTo(existingBooking.getEndDate());
    }

    @Test
    void testDeleteBookingById() {
        // Arrange
        bookingStorage.addBooking(booking);

        // Act
        bookingStorage.deleteBookingById(1L);

        // Assert
        Optional<Booking> deletedBooking = bookingStorage.findBookingById(1L);
        assertThat(deletedBooking).isEmpty();
    }

    @Test
    void testDeleteAllBookings() {
        // Arrange
        bookingStorage.addBooking(booking);

        // Act
        bookingStorage.deleteAllBookings();

        // Assert
        Collection<Booking> bookings = bookingStorage.getAllBookings();
        assertThat(bookings).isEmpty();
    }
}