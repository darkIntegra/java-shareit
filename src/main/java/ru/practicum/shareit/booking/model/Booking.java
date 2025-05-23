package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
//интересная новая аннотация, позволяет изменять выборочно поля, пример "объект.toBuilder().update_поле.build();"
@Builder(toBuilder = true)
public class Booking {
    private Long bookingId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}
