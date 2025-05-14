package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder(toBuilder = true)
public class Item {
    private Long itemId;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
    // возможно, это тоже нужно private String feedback;
}
