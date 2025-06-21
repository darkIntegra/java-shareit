package ru.practicum.shareit.server.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.dto.item.ItemShortDto;
import ru.practicum.shareit.server.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    private UserShortDto booker;

    private ItemShortDto item;
}