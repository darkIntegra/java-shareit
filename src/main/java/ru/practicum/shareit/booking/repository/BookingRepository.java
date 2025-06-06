package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Поиск бронирований по ID арендатора
    List<Booking> findByBookerId(Long bookerId);

    // Поиск бронирований по ID вещи
    List<Booking> findByItemId(Long itemId);

    // Поиск бронирований для владельца вещей
    List<Booking> findByItem_Owner_Id(Long ownerId);

    // Поиск последнего завершенного бронирования для вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.endDate < :now " +
            "ORDER BY b.endDate DESC")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Поиск ближайшего будущего бронирования для вещи
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.startDate > :now " +
            "ORDER BY b.startDate ASC")
    Optional<Booking> findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    // Проверка, что пользователь арендовал вещь (с учетом статуса APPROVED)
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate <= CURRENT_TIMESTAMP")
    boolean existsByUserAndItemAndApprovedStatus(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND ((b.startDate <= :end AND b.endDate >= :start))")
        // Пересечение временных интервалов
    boolean existsByItemIdAndDateOverlap(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Проверка завершенного подтвержденного бронирования
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate < :now")
    boolean existsByUserAndItemAndApprovedStatusAndEndDateBefore(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );
}