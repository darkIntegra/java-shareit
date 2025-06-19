package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerExceptionHandlerTest {

    private final ServerExceptionHandler exceptionHandler = new ServerExceptionHandler();

    @Test
    void handleNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        NotFoundException exception = new NotFoundException("Resource not found");

        // Act
        ErrorResponse response = exceptionHandler.handleNotFoundException(exception);

        // Assert
        assertEquals("Ресурс не найден", response.getError());
        assertEquals("Resource not found", response.getMessage());
    }

    @Test
    void handleRequestNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        RequestNotFoundException exception = new RequestNotFoundException("Request not found");

        // Act
        ErrorResponse response = exceptionHandler.handleRequestNotFoundException(exception);

        // Assert
        assertEquals("Запрос не найден", response.getError());
        assertEquals("Request not found", response.getMessage());
    }

    @Test
    void handleDataConflictException_ShouldReturnConflictResponse() {
        // Arrange
        DataConflictException exception = new DataConflictException("Data conflict occurred");

        // Act
        ErrorResponse response = exceptionHandler.handleDataConflictException(exception);

        // Assert
        assertEquals("Конфликт данных", response.getError());
        assertEquals("Data conflict occurred", response.getMessage());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new Exception("Internal server error");

        // Act
        ErrorResponse response = exceptionHandler.handleGeneralException(exception);

        // Assert
        assertEquals("Внутренняя ошибка сервера", response.getError());
        assertEquals("Internal server error", response.getMessage());
    }
}