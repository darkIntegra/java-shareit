package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingDto dto) {
        // Проверяем существование пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        // Проверяем существование вещи
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + dto.getItemId() + " не найдена"));

        // Проверяем, что вещь доступна для бронирования
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь с ID=" + dto.getItemId() + " недоступна для бронирования");
        }

        // Создаем бронирование
        Booking booking = BookingMapper.toBooking(dto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        // Сохраняем бронирование
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        // Находим бронирование
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Бронирование с ID=" + bookingId + " не найдено"));

        // Проверяем права владельца
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        // Обновляем статус
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        return BookingMapper.toBookingDto(
                bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new NotFoundException("Бронирование с ID=" + bookingId + " не найдено"))
        );
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId) {
        Logger log = LoggerFactory.getLogger(this.getClass());

        // Логируем входные параметры
        log.info("Получен запрос: userId={}, state={}, requesterId={}", userId, state, requesterId);

        // Сравниваем userId и requesterId
        if (!Objects.equals(userId, requesterId)) {
            log.warn("Доступ запрещён: userId={}, requesterId={}", userId, requesterId);
            throw new ForbiddenException("Пользователь с ID=" + requesterId + " не имеет прав доступа");
        }

        log.info("Права доступа подтверждены");

        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователь не найден: userId={}", userId);
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }

        log.info("Пользователь найден: userId={}", userId);

        // Получаем все бронирования пользователя
        var bookings = bookingRepository.findByBookerId(userId);
        log.info("Найдено {} бронирований для пользователя с ID={}", bookings.size(), userId);

        // Фильтруем по состоянию
        var filteredBookings = filterBookingsByState(bookings, state);
        log.info("После фильтрации по состоянию '{}' осталось {} бронирований", state, filteredBookings.size());

        return filteredBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID=" + ownerId + " не найден");
        }

        // Получаем все бронирования для владельца
        var bookings = bookingRepository.findByOwnerId(ownerId);

        // Фильтруем по состоянию
        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookings(BookingState state, Long requesterId) {
        // Проверяем права доступа
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Пользователь с ID=" + requesterId + " не найден");
        }

        // Получаем все бронирования
        var bookings = bookingRepository.findByBookerId(requesterId);

        // Фильтруем по состоянию
        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, BookingState state) {
        return switch (state) {
            case ALL -> bookings;
            case CURRENT -> bookings.stream()
                    .filter(b -> b.getStartDate()
                            .isBefore(LocalDateTime.now()) && b.getEndDate()
                            .isAfter(LocalDateTime.now()))
                    .toList();
            case PAST -> bookings.stream()
                    .filter(b -> b.getEndDate().isBefore(LocalDateTime.now()))
                    .toList();
            case FUTURE -> bookings.stream()
                    .filter(b -> b.getStartDate().isAfter(LocalDateTime.now()))
                    .toList();
            case WAITING -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .toList();
            case REJECTED -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .toList();
        };
    }
}