package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Profile("in-memory")
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Item addItem(Item item) {
        item.setId(nextId.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item updatedItem) {
        updatedItem.setId(itemId);
        items.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    @Override
    public Collection<Item> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }
        items.remove(itemId);
    }

    @Override
    public void deleteAllItems() {
        items.clear();
    }
}
