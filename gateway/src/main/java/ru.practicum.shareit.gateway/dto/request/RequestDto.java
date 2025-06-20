package ru.practicum.shareit.gateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.dto.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}