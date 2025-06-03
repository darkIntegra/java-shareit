package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingDto dto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId);

    List<BookingDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId);

    List<BookingDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state);

    List<BookingDto> getAllBookings(BookingState state, Long requesterId);
}