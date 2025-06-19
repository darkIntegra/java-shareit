package ru.practicum.shareit.server.service.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.repository.booking.BookingRepository;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.ForbiddenException;

import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.mapper.item.CommentMapper;
import ru.practicum.shareit.server.mapper.item.ItemMapper;
import ru.practicum.shareit.server.model.item.Comment;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.repository.item.CommentRepository;
import ru.practicum.shareit.server.storage.item.ItemStorage;
import ru.practicum.shareit.server.storage.request.RequestStorage;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        // Проверяем существование пользователя
        User owner = userStorage.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        // Если requestId указан, проверяем существование запроса
        if (dto.getRequestId() != null) {
            requestStorage.findRequestById(dto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("Запрос с ID=" + dto.getRequestId() + " не найден"));
        }

        // Создаем вещь
        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        // Сохраняем вещь
        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        // Находим вещь
        Item existingItem = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

        // Проверяем права владельца
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        // Обновляем поля
        if (dto.getName() != null) existingItem.setName(dto.getName());
        if (dto.getDescription() != null) existingItem.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existingItem.setAvailable(dto.getAvailable());

        // Сохраняем обновлённую вещь
        Item updatedItem = itemStorage.updateItem(itemId, existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        return ItemMapper.toItemDtoWithComments(item, comments); // Обновленный маппер
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        // Получаем вещи пользователя
        Collection<Item> items = itemStorage.getItemsByOwnerId(userId);
        // Добавляем информацию о бронированиях
        return items.stream()
                .map(this::mapItemWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        Collection<Item> items = itemStorage.getItemsByOwnerId(ownerId);
        return items.stream()
                .map(this::mapItemWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        return itemStorage.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemStorage.deleteItemById(itemId);
    }

    @Override
    public void deleteAllItems() {
        itemStorage.deleteAllItems();
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        // 1. Проверяем существование пользователя
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + userId + " не найден"));

        // 2. Проверяем существование вещи
        Item item = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена"));

        // 3. Проверяем, что пользователь арендовал вещь с подтвержденным статусом
        boolean hasBooking = bookingRepository.existsByUserAndItemAndApprovedStatus(userId, itemId);
        if (!hasBooking) {
            throw new BadRequestException("Пользователь с ID=" + userId + " не имеет права оставлять комментарий");
        }

        // 4. Проверяем, что бронирование завершено
        boolean isBookingCompleted = bookingRepository.existsByUserAndItemAndApprovedStatusAndEndDateBefore(
                userId, itemId, LocalDateTime.now());
        if (!isBookingCompleted) {
            throw new BadRequestException("Бронирование для пользователя с ID=" + userId + " еще не завершено");
        }

        // 5. Создаем комментарий
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        // 6. Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    // Вспомогательный метод для добавления бронирований в DTO
    private ItemDto mapItemWithBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), now).orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), now).orElse(null);
        return ItemMapper.toItemDtoWithBookings(item, lastBooking, nextBooking);
    }
}