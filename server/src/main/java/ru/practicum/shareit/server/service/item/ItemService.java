package ru.practicum.shareit.server.service.item;

import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto dto);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getAllItems(Long userId);

    Collection<ItemDto> getItemsByOwnerId(Long ownerId);

    void deleteItemById(Long itemId);

    void deleteAllItems();

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    Collection<ItemDto> searchItems(String text);
}
