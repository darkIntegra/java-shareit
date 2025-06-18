package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.booking.BookingCreateDto;
import ru.practicum.shareit.gateway.dto.booking.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingCreateDto createDto) {
        return bookingClient.addBooking(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId) {
        return bookingClient.getBookingById(bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingClient.getAllBookings(state, requesterId);
    }

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<Object> getAllBookingsByUser(
            @PathVariable Long userId,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingClient.getAllBookingsByUser(userId, state, requesterId);
    }

    @GetMapping("/bookings/owner")
    public ResponseEntity<Object> getAllBookingsForOwnerItems(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingClient.getAllBookingsForOwnerItems(ownerId, state);
    }
}