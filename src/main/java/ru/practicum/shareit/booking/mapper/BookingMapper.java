package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;

@Component
public class BookingMapper {

    public static Booking toBooking(BookingDto dto) {
        return Booking.builder()
                .id(dto.getId())
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .build();
    }

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
}