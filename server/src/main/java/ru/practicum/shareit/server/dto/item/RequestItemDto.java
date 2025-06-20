package ru.practicum.shareit.server.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDto {

    private Long id;

    private String name;

    private Long ownerId;

    private LocalDateTime created;
}
