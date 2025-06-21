package ru.practicum.shareit.gateway.dto.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.gateway.dto.item.ItemShortDto;
import ru.practicum.shareit.gateway.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Поддержка Java 8 типов (LocalDateTime)

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialization() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .itemId(2L)
                .bookerId(3L)
                .status(BookingStatus.APPROVED)
                .booker(UserShortDto.builder().id(3L).build()) // Используем только id
                .item(ItemShortDto.builder().id(2L).name("Item Name").build())
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("id", "start", "end", "itemId", "bookerId", "status", "booker", "item");
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String json = """
                {
                    "id": 1,
                    "start": "2023-10-01T12:00:00",
                    "end": "2023-10-02T12:00:00",
                    "itemId": 2,
                    "bookerId": 3,
                    "status": "APPROVED",
                    "booker": {
                        "id": 3
                    },
                    "item": {
                        "id": 2,
                        "name": "Item Name"
                    }
                }
                """;

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertEquals(1L, dto.getId());
        assertEquals(LocalDateTime.parse("2023-10-01T12:00:00"), dto.getStart());
        assertEquals(LocalDateTime.parse("2023-10-02T12:00:00"), dto.getEnd());
        assertEquals(2L, dto.getItemId());
        assertEquals(3L, dto.getBookerId());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(3L, dto.getBooker().getId()); // Проверяем только id
        assertEquals("Item Name", dto.getItem().getName());
    }

    @Test
    void testValidation_ValidDto() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(now.plusHours(1))
                .end(now.plusHours(2))
                .itemId(2L)
                .bookerId(3L)
                .status(BookingStatus.APPROVED)
                .booker(UserShortDto.builder().id(3L).build())
                .item(ItemShortDto.builder().id(2L).name("Item Name").build())
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidDto() {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1)) // Нарушение @FutureOrPresent
                .end(LocalDateTime.now().minusDays(2))  // Нарушение @Future
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);
        violations.forEach(violation -> {
            if (violation.getMessage().contains("start")) {
                assertThat(violation.getMessage()).isEqualTo("Дата начала должна быть в будущем или настоящем");
            } else if (violation.getMessage().contains("end")) {
                assertThat(violation.getMessage()).isEqualTo("Дата окончания должна быть в будущем");
            }
        });
    }
}