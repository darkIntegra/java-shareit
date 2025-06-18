package ru.practicum.shareit.gateway.dto.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.gateway.dto.booking.BookingShortDto;
import ru.practicum.shareit.gateway.validation.OnCreate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Поддержка Java 8 типов (LocalDateTime)

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws JsonProcessingException {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .lastBooking(BookingShortDto.builder().id(1L).build())
                .nextBooking(BookingShortDto.builder().id(2L).build())
                .comments(List.of(
                        CommentDto.builder().id(1L).text("Great item!").authorName("John Doe").created(LocalDateTime.now()).build()
                ))
                .requestId(3L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("id", "name", "description", "available", "lastBooking", "nextBooking", "comments", "requestId");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "id": 1,
                    "name": "Item Name",
                    "description": "Item Description",
                    "available": true,
                    "lastBooking": {
                        "id": 1
                    },
                    "nextBooking": {
                        "id": 2
                    },
                    "comments": [
                        {
                            "id": 1,
                            "text": "Great item!",
                            "authorName": "John Doe",
                            "created": "2023-10-01T12:00:00"
                        }
                    ],
                    "requestId": 3
                }
                """;

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item Name");
        assertThat(dto.getDescription()).isEqualTo("Item Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getNextBooking().getId()).isEqualTo(2L);
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Great item!");
        assertThat(dto.getRequestId()).isEqualTo(3L);
    }

    @Test
    void testValidation_ValidDto() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidDto() {
        ItemDto dto = ItemDto.builder()
                .name("") // Нарушение @NotBlank
                .build();

        // Указываем группу валидации (OnCreate)
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto, OnCreate.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Поле 'name' не может быть пустым");
    }
}