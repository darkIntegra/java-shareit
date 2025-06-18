package ru.practicum.shareit.gateway.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.dto.item.ItemShortDto;
import ru.practicum.shareit.gateway.dto.user.UserShortDto;

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