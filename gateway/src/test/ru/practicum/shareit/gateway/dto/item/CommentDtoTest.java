package ru.practicum.shareit.gateway.dto.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Поддержка Java 8 типов (LocalDateTime)

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws JsonProcessingException {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("id", "text", "authorName", "created");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "id": 1,
                    "text": "Great item!",
                    "authorName": "John Doe",
                    "created": "2023-10-01T12:00:00"
                }
                """;

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("John Doe");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.parse("2023-10-01T12:00:00"));
    }

    @Test
    void testValidation_ValidDto() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidDto() {
        CommentDto dto = CommentDto.builder()
                .text("") // Нарушение @NotBlank
                .build();

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Текст комментария не может быть пустым");
    }
}