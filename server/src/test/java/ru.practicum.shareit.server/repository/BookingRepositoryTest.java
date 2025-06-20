package ru.practicum.shareit.server.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.booking.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        testEntityManager.persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        testEntityManager.persist(booker);

        // Создаем вещь
        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        testEntityManager.persist(item);

        // Создаем бронирования
        booking1 = new Booking();
        booking1.setStartDate(LocalDateTime.now().minusDays(5));
        booking1.setEndDate(LocalDateTime.now().minusDays(3));
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.APPROVED);
        testEntityManager.persist(booking1);

        booking2 = new Booking();
        booking2.setStartDate(LocalDateTime.now().plusDays(1));
        booking2.setEndDate(LocalDateTime.now().plusDays(3));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.APPROVED);
        testEntityManager.persist(booking2);
    }

    @AfterEach
    void tearDown() {
        // Получаем EntityManager из TestEntityManager
        EntityManager entityManager = testEntityManager.getEntityManager();

        // Очищаем таблицы в обратном порядке (сначала зависимые таблицы)
        entityManager.createQuery("DELETE FROM Booking").executeUpdate();
        entityManager.createQuery("DELETE FROM Item").executeUpdate();
        entityManager.createQuery("DELETE FROM User").executeUpdate();
    }

    @Test
    void testFindByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId());
        assertThat(bookings).hasSize(2);
        assertThat(bookings).contains(booking1, booking2);
    }

    @Test
    void testFindByItemId() {
        List<Booking> bookings = bookingRepository.findByItemId(item.getId());
        assertThat(bookings).hasSize(2);
        assertThat(bookings).contains(booking1, booking2);
    }

    @Test
    void testFindByItem_Owner_Id() {
        List<Booking> bookings = bookingRepository.findByItem_Owner_Id(owner.getId());
        assertThat(bookings).hasSize(2);
        assertThat(bookings).contains(booking1, booking2);
    }

    @Test
    void testFindLastBooking() {
        Optional<Booking> lastBooking = bookingRepository.findLastBooking(
                item.getId(),
                LocalDateTime.now()
        );
        assertThat(lastBooking).isPresent();
        assertThat(lastBooking.get()).isEqualTo(booking1);
    }

    @Test
    void testFindNextBooking() {
        Optional<Booking> nextBooking = bookingRepository.findNextBooking(
                item.getId(),
                LocalDateTime.now()
        );
        assertThat(nextBooking).isPresent();
        assertThat(nextBooking.get()).isEqualTo(booking2);
    }

    @Test
    void testExistsByUserAndItemAndApprovedStatus() {
        boolean exists = bookingRepository.existsByUserAndItemAndApprovedStatus(
                booker.getId(),
                item.getId()
        );
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByItemIdAndDateOverlap() {
        boolean exists = bookingRepository.existsByItemIdAndDateOverlap(
                item.getId(),
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2)
        );
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByUserAndItemAndApprovedStatusAndEndDateBefore() {
        boolean exists = bookingRepository.existsByUserAndItemAndApprovedStatusAndEndDateBefore(
                booker.getId(),
                item.getId(),
                LocalDateTime.now()
        );
        assertThat(exists).isTrue();
    }
}