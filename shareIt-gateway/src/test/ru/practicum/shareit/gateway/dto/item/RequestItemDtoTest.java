package ru.practicum.shareit.gateway.dto.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestItemDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws JsonProcessingException {
        RequestItemDto dto = RequestItemDto.builder()
                .id(1L)
                .name("Item Name")
                .ownerId(2L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("id", "name", "ownerId");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "id": 1,
                    "name": "Item Name",
                    "ownerId": 2
                }
                """;

        RequestItemDto dto = objectMapper.readValue(json, RequestItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item Name");
        assertThat(dto.getOwnerId()).isEqualTo(2L);
    }

    @Test
    void testValidation_ValidDto() {
        RequestItemDto dto = RequestItemDto.builder()
                .id(1L)
                .name("Item Name")
                .ownerId(2L)
                .build();

        Set<ConstraintViolation<RequestItemDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidDto() {
        RequestItemDto dto = RequestItemDto.builder()
                .name("") // Нарушение @NotBlank
                .build();

        Set<ConstraintViolation<RequestItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Название вещи не может быть пустым");
    }
}