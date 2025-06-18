package ru.practicum.shareit.gateway.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.dto.booking.BookingShortDto;
import ru.practicum.shareit.gateway.validation.OnCreate;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Поле 'name' не может быть пустым")
    private String name;

    private String description;

    private Boolean available;

    private BookingShortDto lastBooking; // Последнее бронирование

    private BookingShortDto nextBooking; // Ближайшее бронирование

    private List<CommentDto> comments;

    private Long requestId; // ID запроса, на который отвечает вещь (необязательное поле)
}