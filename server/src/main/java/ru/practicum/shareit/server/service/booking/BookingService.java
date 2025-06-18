package ru.practicum.shareit.server.service.booking;

import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingCreateDto createDto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long bookingId);

    List<BookingShortDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId);

    List<BookingShortDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state);

    List<BookingDto> getAllBookings(BookingState state, Long requesterId);
}