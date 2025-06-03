package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL, // Все бронирования
    CURRENT, // Текущие
    PAST, // Завершённые
    FUTURE, // Будущие
    WAITING, // Ожидающие подтверждения
    REJECTED // Отклонённые
}