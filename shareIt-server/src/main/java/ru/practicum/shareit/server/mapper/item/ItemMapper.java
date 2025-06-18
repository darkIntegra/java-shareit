package ru.practicum.shareit.server.mapper.item;

import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.booking.Booking;

import java.util.List;

public class ItemMapper {

    // Преобразование Item в ItemDto
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    // Преобразование ItemDto в Item
    public static Item toItem(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .requestId(dto.getRequestId())
                .build();
    }

    // Преобразование Item в ItemDto с учетом бронирований
    public static ItemDto toItemDtoWithBookings(Item item, Booking lastBooking, Booking nextBooking) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(lastBooking != null ? toBookingShortDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? toBookingShortDto(nextBooking) : null)
                .build();
    }

    // Преобразование Booking в BookingShortDto
    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate()) // Преобразуем startDate в start
                .end(booking.getEndDate())     // Преобразуем endDate в end
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static ItemDto toItemDtoWithComments(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .comments(comments)
                .build();
    }
}