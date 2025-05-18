package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto dto) {
        if (!userStorage.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с ID=" + userId + " не найден");
        }

        User owner = userStorage.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID=" + userId + " не найден"));

        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);

        Item savedItem = itemStorage.addItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        // Находим существующую вещь
        Item existingItem = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));

        // Проверяем права владельца
        if (!Objects.equals(existingItem.getOwner().getUserId(), userId)) {
            throw new ForbiddenException("Пользователь с ID=" + userId + " не является владельцем вещи");
        }

        // Преобразуем существующую вещь в DTO
        ItemDto existingItemDto = ItemMapper.toItemDto(existingItem);

        // Обновляем только переданные поля
        ItemDto updatedItemDto = existingItemDto.toBuilder()
                .name(dto.getName() != null ? dto.getName() : existingItemDto.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : existingItemDto.getDescription())
                .available(dto.getAvailable() != null ? dto.getAvailable() : existingItemDto.getAvailable())
                .build();

        // Преобразуем обновлённый DTO обратно в Item
        Item updatedItem = ItemMapper.toItem(updatedItemDto);
        updatedItem.setOwner(existingItem.getOwner()); // Владелец остаётся неизменным

        // Сохраняем обновлённую вещь через хранилище
        Item savedItem = itemStorage.updateItem(itemId, updatedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(
                itemStorage.findItemById(itemId)
                        .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"))
        );
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemStorage.getItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemStorage.deleteItemById(itemId);
    }

    @Override
    public void deleteAllItems() {
        itemStorage.deleteAllItems();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList(); // Если текст пустой, возвращаем пустой список
        }

        String lowerCaseText = text.toLowerCase(); // Преобразуем текст для сравнения без учёта регистра

        return itemStorage.getAllItems().stream()
                .filter(Item::getAvailable) // Фильтруем только доступные вещи
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText))
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
