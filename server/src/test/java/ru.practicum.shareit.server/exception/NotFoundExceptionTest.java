package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void notFoundException_ShouldHaveCorrectMessage() {
        // Arrange
        String expectedMessage = "Resource not found";

        // Act
        NotFoundException exception = new NotFoundException(expectedMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Сообщение исключения должно совпадать с ожидаемым");
    }
}