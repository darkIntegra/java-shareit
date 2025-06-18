package ru.practicum.shareit.server.mapper.booking;

import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.dto.item.ItemShortDto;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.dto.user.UserShortDto;
import ru.practicum.shareit.server.model.user.User;

public class BookingMapper {

    // Преобразование Entity в полную DTO (BookingDto)
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .itemId(booking.getItem().getId())
                .status(booking.getStatus())
                .booker(UserShortDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(ItemShortDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .build();
    }

    // Преобразование Entity в краткую DTO (BookingShortDto)
    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    // Преобразование полной DTO (BookingDto) в Entity
    public static Booking toBooking(BookingDto dto) {
        return Booking.builder()
                .id(dto.getId())
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .build();
    }

    // Преобразование краткой DTO (BookingShortDto) в Entity
    public static Booking toBooking(BookingShortDto dto) {
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .build();
    }

    // Преобразование BookingCreateDto в Entity
    public static Booking toBooking(BookingCreateDto createDto, Item item, User booker) {
        return Booking.builder()
                .startDate(createDto.getStart())
                .endDate(createDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING) // Устанавливаем статус WAITING
                .build();
    }
}