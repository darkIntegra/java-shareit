package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid BookingDto dto) {
        return bookingService.addBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId,
                                    @RequestParam Boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {

        log.info("Получен запрос на получение всех бронирований: requesterId={}, state={}", requesterId, state);

        // Проверяем права доступа
        if (requesterId == null) {
            throw new IllegalArgumentException("Заголовок 'X-Sharer-User-Id' обязателен");
        }

        // Вызываем метод сервиса
        return bookingService.getAllBookings(state, requesterId);
    }

    @GetMapping("/users/{userId}/bookings")
    public List<BookingDto> getAllBookingsByUser(
            @PathVariable Long userId,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {

        log.info("Получен запрос: userId={}, requesterId={}, state={}", userId, requesterId, state);

        // Проверяем права доступа
        if (!Objects.equals(userId, requesterId)) {
            log.warn("Доступ запрещён: userId={}, requesterId={}", userId, requesterId);
            throw new ForbiddenException("Пользователь с ID=" + requesterId + " не имеет прав доступа");
        }

        log.info("Права доступа подтверждены");

        return bookingService.getAllBookingsByUser(userId, state, requesterId);
    }

    @GetMapping("/bookings/owner")
    public List<BookingDto> getAllBookingsForOwnerItems(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {

        log.info("Получен запрос на получение бронирований владельца: ownerId={}, state={}", ownerId, state);

        // Проверяем, что ownerId передан
        if (ownerId == null) {
            throw new IllegalArgumentException("Заголовок 'X-Sharer-User-Id' обязателен");
        }

        // Вызываем метод сервиса
        return bookingService.getAllBookingsForOwnerItems(ownerId, state);
    }
}