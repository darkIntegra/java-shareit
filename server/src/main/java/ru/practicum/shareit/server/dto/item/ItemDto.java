package ru.practicum.shareit.server.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShortDto lastBooking; // Последнее бронирование

    private BookingShortDto nextBooking; // Ближайшее бронирование

    private List<CommentDto> comments;

    private Long requestId; // ID запроса, на который отвечает вещь (необязательное поле)
}