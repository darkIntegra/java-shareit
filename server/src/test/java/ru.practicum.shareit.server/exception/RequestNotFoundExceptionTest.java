package ru.practicum.shareit.server.exception;

import exception.RequestNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestNotFoundExceptionTest {

    @Test
    void requestNotFoundException_ShouldHaveCorrectMessage() {
        // Arrange
        String expectedMessage = "Request not found";

        // Act
        RequestNotFoundException exception = new RequestNotFoundException(expectedMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Сообщение исключения должно совпадать с ожидаемым");
    }
}