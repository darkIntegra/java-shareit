package ru.practicum.shareit.gateway.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.gateway.validation.OnCreate;
import ru.practicum.shareit.gateway.validation.OnUpdate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "email не может быть пустым", groups = OnCreate.class)
    @Email(message = "Некорректный формат email", groups = {OnCreate.class, OnUpdate.class})
    private String email;
}