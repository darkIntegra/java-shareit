//package ru.practicum.shareit.server.storage;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import ru.practicum.shareit.server.model.booking.Booking;
//import ru.practicum.shareit.server.model.booking.BookingStatus;
//import ru.practicum.shareit.server.model.item.Item;
//import ru.practicum.shareit.server.model.user.User;
//import ru.practicum.shareit.server.storage.booking.BookingStorage;
//
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("in-memory")
//class InMemoryBookingStorageTest {
//
//    @Autowired
//    private BookingStorage bookingStorage;
//
//    private User booker;
//    private Item item;
//    private Booking booking;
//
//    @BeforeEach
//    void setUp() {
//        // Очищаем хранилище
//        bookingStorage.deleteAllBookings();
//
//        // Создаем пользователя (владельца вещи)
//        User owner = new User();
//        owner.setId(1L);
//        owner.setName("Owner");
//        owner.setEmail("owner@example.com");
//
//        // Создаем пользователя (арендатора)
//        booker = new User();
//        booker.setId(2L);
//        booker.setName("Booker");
//        booker.setEmail("booker@example.com");
//
//        // Создаем вещь
//        item = new Item();
//        item.setId(1L);
//        item.setName("Bike");
//        item.setDescription("Mountain bike for rent");
//        item.setAvailable(true);
//        item.setOwner(owner);
//
//        // Создаем бронирование
//        booking = new Booking();
//        booking.setStartDate(LocalDateTime.now().minusDays(5));
//        booking.setEndDate(LocalDateTime.now().minusDays(3));
//        booking.setStatus(BookingStatus.APPROVED);
//        booking.setBooker(booker);
//        booking.setItem(item);
//    }
//
//    @Test
//    void testAddBooking() {
//        // Act
//        Booking savedBooking = bookingStorage.addBooking(booking);
//
//        // Assert
//        assertThat(savedBooking).isNotNull();
//        Long bookingId = savedBooking.getId();
//        assertThat(bookingId).isNotNull();
//        assertThat(bookingStorage.findBookingById(bookingId)).isPresent();
//    }
//
//    @Test
//    void testFindBookingById() {
//        // Arrange
//        Booking savedBooking = bookingStorage.addBooking(booking);
//        Long bookingId = savedBooking.getId();
//
//        // Act
//        Optional<Booking> foundBooking = bookingStorage.findBookingById(bookingId);
//
//        // Assert
//        assertThat(foundBooking).isPresent();
//        assertThat(foundBooking.get().getId()).isEqualTo(bookingId);
//        assertThat(foundBooking.get().getBooker().getId()).isEqualTo(2L);
//        assertThat(foundBooking.get().getItem().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    void testGetAllBookings() {
//        // Arrange
//        Booking savedBooking = bookingStorage.addBooking(booking); // Сохраняем бронирование
//        Long bookingId = savedBooking.getId(); // Получаем реальный ID
//
//        // Act
//        Collection<Booking> bookings = bookingStorage.getAllBookings();
//
//        // Assert
//        assertThat(bookings).hasSize(1);
//        Booking firstBooking = bookings.iterator().next();
//        assertThat(firstBooking.getId()).isEqualTo(bookingId); // Проверяем реальный ID
//        assertThat(firstBooking.getBooker().getId()).isEqualTo(2L);
//        assertThat(firstBooking.getItem().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    void testUpdateBooking() {
//        // Arrange
//        Booking savedBooking = bookingStorage.addBooking(booking); // Сохраняем бронирование
//        Long bookingId = savedBooking.getId();
//
//        // Создаем обновленное бронирование
//        Booking updatedBooking = new Booking();
//        updatedBooking.setStartDate(LocalDateTime.now().minusDays(4));
//        updatedBooking.setEndDate(LocalDateTime.now().minusDays(2));
//        updatedBooking.setStatus(BookingStatus.REJECTED);
//        updatedBooking.setBooker(booker);
//        updatedBooking.setItem(item);
//
//        // Act
//        Booking savedUpdatedBooking = bookingStorage.updateBooking(bookingId, updatedBooking);
//
//        // Assert
//        assertThat(savedUpdatedBooking).isNotNull();
//        assertThat(savedUpdatedBooking.getId()).isEqualTo(bookingId);
//        assertThat(savedUpdatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
//        assertThat(savedUpdatedBooking.getStartDate()).isEqualTo(updatedBooking.getStartDate());
//        assertThat(savedUpdatedBooking.getEndDate()).isEqualTo(updatedBooking.getEndDate());
//    }
//
//    @Test
//    void testDeleteBookingById() {
//        // Arrange
//        Booking savedBooking = bookingStorage.addBooking(booking);
//        Long bookingId = savedBooking.getId();
//
//        // Act
//        bookingStorage.deleteBookingById(bookingId);
//
//        // Assert
//        Optional<Booking> deletedBooking = bookingStorage.findBookingById(bookingId);
//        assertThat(deletedBooking).isEmpty();
//    }
//
//    @Test
//    void testDeleteAllBookings() {
//        // Arrange
//        bookingStorage.addBooking(booking);
//
//        // Act
//        bookingStorage.deleteAllBookings();
//
//        // Assert
//        Collection<Booking> bookings = bookingStorage.getAllBookings();
//        assertThat(bookings).isEmpty();
//    }
//
//    @Test
//    void testGetBookingsByBookerId() {
//        // Arrange
//        bookingStorage.addBooking(booking);
//
//        // Act
//        Collection<Booking> bookings = bookingStorage.getBookingsByBookerId(2L);
//
//        // Assert
//        assertThat(bookings).hasSize(1);
//        Booking firstBooking = bookings.iterator().next();
//        assertThat(firstBooking.getBooker().getId()).isEqualTo(2L);
//    }
//
//    @Test
//    void testGetBookingsByItemId() {
//        // Arrange
//        bookingStorage.addBooking(booking);
//
//        // Act
//        Collection<Booking> bookings = bookingStorage.getBookingsByItemId(1L);
//
//        // Assert
//        assertThat(bookings).hasSize(1);
//        Booking firstBooking = bookings.iterator().next();
//        assertThat(firstBooking.getItem().getId()).isEqualTo(1L);
//    }
//}