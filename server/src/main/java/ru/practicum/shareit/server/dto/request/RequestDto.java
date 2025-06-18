package ru.practicum.shareit.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.dto.item.RequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<RequestItemDto> items;
}