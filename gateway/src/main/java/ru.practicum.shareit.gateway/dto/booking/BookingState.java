package ru.practicum.shareit.gateway.dto.booking;

public enum BookingState {
    ALL, // Все бронирования
    CURRENT, // Текущие
    PAST, // Завершённые
    FUTURE, // Будущие
    WAITING, // Ожидающие подтверждения
    REJECTED // Отклонённые
}