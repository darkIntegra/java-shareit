package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "Поле 'start' не может быть пустым")
    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;

    @NotNull(message = "Поле 'end' не может быть пустым")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    private UserShortDto booker;

    private ItemShortDto item;
}