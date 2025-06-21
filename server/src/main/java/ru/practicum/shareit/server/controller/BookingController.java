package ru.practicum.shareit.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.dto.booking.*;
import ru.practicum.shareit.server.service.booking.BookingService;

import java.util.List;

import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    // Создание нового бронирования
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestBody BookingCreateDto createDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST-запрос на создание бронирования: {}, userId={}", createDto, userId);
        return bookingService.addBooking(userId, createDto);
    }

    // Обновление статуса бронирования
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto updateBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH-запрос на обновление бронирования: bookingId={}, approved={}, userId={}",
                bookingId, approved, userId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    // Получение бронирования по ID
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        log.info("GET-запрос на получение бронирования: bookingId={}", bookingId);
        return bookingService.getBookingById(bookingId);
    }

    // Получение всех бронирований текущего пользователя
    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("GET-запрос на получение всех бронирований: state={}, requesterId={}", state, requesterId);
        return bookingService.getAllBookings(state, requesterId);
    }

    // Получение всех бронирований для конкретного пользователя
    @GetMapping("/users/{userId}/bookings")
    public List<BookingShortDto> getAllBookingsByUser(
            @PathVariable Long userId,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("GET-запрос на получение бронирований для пользователя: userId={}, state={}, requesterId={}",
                userId, state, requesterId);
        return bookingService.getAllBookingsByUser(userId, state, requesterId);
    }

    // Получение всех бронирований для вещей, принадлежащих владельцу
    @GetMapping("/bookings/owner")
    public List<BookingShortDto> getAllBookingsForOwnerItems(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET-запрос на получение бронирований для вещей владельца: state={}, ownerId={}", state, ownerId);
        return bookingService.getAllBookingsForOwnerItems(ownerId, state);
    }
}