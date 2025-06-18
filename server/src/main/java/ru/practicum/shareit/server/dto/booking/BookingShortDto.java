package ru.practicum.shareit.server.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;
}