package ru.practicum.shareit.server.storage.item;

import ru.practicum.shareit.server.model.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Long itemId, Item updatedItem);

    Optional<Item> findItemById(Long itemId);

    Collection<Item> getAllItems();

    Collection<Item> getItemsByOwnerId(Long ownerId);

    void deleteItemById(Long itemId);

    void deleteAllItems();
}