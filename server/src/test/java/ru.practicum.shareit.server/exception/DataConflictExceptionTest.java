package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataConflictExceptionTest {

    @Test
    void dataConflictException_ShouldHaveCorrectMessage() {
        // Arrange
        String expectedMessage = "Data conflict occurred";

        // Act
        DataConflictException exception = new DataConflictException(expectedMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Сообщение исключения должно совпадать с ожидаемым");
    }
}