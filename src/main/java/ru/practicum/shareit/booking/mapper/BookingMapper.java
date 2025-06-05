package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

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