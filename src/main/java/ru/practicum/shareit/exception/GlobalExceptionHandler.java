package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.dto.ErrorResponse;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка общих ошибок IllegalArgumentException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("Некорректные данные", ex.getMessage());
    }

    // Обработка ошибок NoSuchElementException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
        return new ErrorResponse("Ресурс не найден", ex.getMessage());
    }

    // Обработка всех остальных исключений (резервный обработчик)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneralException(Exception ex) {
        return new ErrorResponse("Внутренняя ошибка сервера", ex.getMessage());
    }

    // Обработка конфликтов (например, дубликат email)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse handleConflictException(ConflictException ex) {
        return new ErrorResponse("Конфликт", ex.getMessage());
    }

    // Обработка отсутствия доступа
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        return new ErrorResponse("Доступ запрещён", ex.getMessage());
    }

    // Обработка ошибок RequestNotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RequestNotFoundException.class)
    public ErrorResponse handleRequestNotFoundException(RequestNotFoundException ex) {
        return new ErrorResponse("Запрос не найден", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return new ErrorResponse("Ресурс не найден", ex.getMessage());
    }

    // Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );
        return new ErrorResponse("Некорректные данные", errorMessage.toString());
    }

    // Обработка BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Bad Request", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}