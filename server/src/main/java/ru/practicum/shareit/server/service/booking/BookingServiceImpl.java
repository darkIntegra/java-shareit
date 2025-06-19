package ru.practicum.shareit.server.service.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;
import ru.practicum.shareit.server.mapper.booking.BookingMapper;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.user.UserRepository;

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
    public BookingDto addBooking(Long userId, BookingCreateDto createDto) {
        // Проверяем существование пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        // Проверяем существование вещи
        Item item = itemRepository.findById(createDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + createDto.getItemId() + " не найдена"));

        // Проверяем, что вещь доступна для бронирования
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь с ID=" + createDto.getItemId() + " недоступна для бронирования");
        }

        // Проверяем пересечение с существующими бронированиями
        boolean isOverlapping = bookingRepository.existsByItemIdAndDateOverlap(
                createDto.getItemId(),
                createDto.getStart(),
                createDto.getEnd()
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("Бронирование пересекается с существующими бронированиями");
        }

        // Создаем бронирование
        Booking booking = BookingMapper.toBooking(createDto, item, booker);

        // Сохраняем бронирование
        Booking savedBooking = bookingRepository.save(booking);

        // Преобразуем в DTO и возвращаем
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
    public List<BookingShortDto> getAllBookingsByUser(Long userId, BookingState state, Long requesterId) {
        // Проверяем права доступа
        if (!Objects.equals(userId, requesterId)) {
            throw new ForbiddenException("Пользователь с ID=" + requesterId + " не имеет прав доступа");
        }

        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }

        // Получаем все бронирования пользователя
        var bookings = bookingRepository.findByBookerId(userId);

        // Фильтруем по состоянию
        var filteredBookings = filterBookingsByState(bookings, state);

        return filteredBookings.stream()
                .map(BookingMapper::toBookingShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingShortDto> getAllBookingsForOwnerItems(Long ownerId, BookingState state) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с ID=" + ownerId + " не найден");
        }

        // Получаем все бронирования для владельца
        var bookings = bookingRepository.findByItem_Owner_Id(ownerId);

        // Фильтруем по состоянию
        return filterBookingsByState(bookings, state).stream()
                .map(BookingMapper::toBookingShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookings(BookingState state, Long requesterId) {
        // Проверяем права доступа
        if (!userRepository.existsById(requesterId)) {
            throw new NotFoundException("Пользователь с ID=" + requesterId + " не найден");
        }

        // Получаем все бронирования пользователя
        List<Booking> bookings = bookingRepository.findByBookerId(requesterId);

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