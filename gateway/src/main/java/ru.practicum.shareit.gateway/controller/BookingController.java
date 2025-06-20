package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.booking.BookingCreateDto;
import ru.practicum.shareit.gateway.dto.booking.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingCreateDto createDto) {
        log.info("POST-запрос на создание бронирования: {}, userId={}", createDto, userId);
        return bookingClient.addBooking(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH-запрос на обновление бронирования: bookingId={}, approved={}, userId={}",
                bookingId, approved, userId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId) {
        log.info("GET-запрос на получение бронирования: bookingId={}", bookingId);
        return bookingClient.getBookingById(bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        log.info("GET-запрос на получение всех бронирований: state={}, requesterId={}", state, requesterId);
        return bookingClient.getAllBookings(state, requesterId);
    }

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<Object> getAllBookingsByUser(
            @PathVariable Long userId,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("GET-запрос на получение бронирований пользователя: userId={}, state={}, requesterId={}",
                userId, state, requesterId);
        return bookingClient.getAllBookingsByUser(userId, state, requesterId);
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getAllBookingsForOwnerItems(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET-запрос на получение бронирований для вещей владельца: state={}, ownerId={}", state, ownerId);
        return bookingClient.getAllBookingsForOwnerItems(ownerId, state);
    }
}