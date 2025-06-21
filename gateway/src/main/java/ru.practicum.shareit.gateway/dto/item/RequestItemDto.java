package ru.practicum.shareit.gateway.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    private Long ownerId;

    @NotNull(message = "Дата создания запроса не может быть null")
    private LocalDateTime created;
}
