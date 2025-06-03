package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        // Проверяем существование пользователя
        User owner = userStorage.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

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
        log.info("Проверка прав для добавления комментария: userId={}, itemId={}", userId, itemId);

        // Проверяем, что пользователь арендовал вещь с подтвержденным статусом
        boolean hasBooking = bookingRepository.existsByUserAndItemAndApprovedStatus(userId, itemId);
        if (!hasBooking) {
            log.warn("Пользователь с ID={} не имеет права оставлять комментарий для вещи с ID={}", userId, itemId);
            throw new BadRequestException("Пользователь с ID=" + userId + " не имеет права оставлять комментарий");
        }

        // Создаем комментарий
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemStorage.findItemById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с ID=" + itemId + " не найдена")));
        comment.setAuthor(userStorage.findUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID=" + userId + " не найден")));
        comment.setCreated(LocalDateTime.now());

        // Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий успешно сохранен: commentId={}", savedComment.getId());

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