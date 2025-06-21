package ru.practicum.shareit.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.service.item.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST-запрос на добавление вещи: {}, userId={}", itemDto, userId);
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST-запрос на добавление комментария: itemId={}, comment={}, userId={}", itemId, commentDto, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH-запрос на обновление вещи: itemId={}, item={}, userId={}", itemId, itemDto, userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("GET-запрос на получение вещи: itemId={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех вещей пользователя: userId={}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping("/owner/{ownerId}")
    public Collection<ItemDto> getItemsByOwnerId(@PathVariable Long ownerId) {
        log.info("GET-запрос на получение вещей владельца: ownerId={}", ownerId);
        return itemService.getItemsByOwnerId(ownerId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable Long itemId) {
        log.info("DELETE-запрос на удаление вещи: itemId={}", itemId);
        itemService.deleteItemById(itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllItems() {
        log.info("DELETE-запрос на удаление всех вещей");
        itemService.deleteAllItems();
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("GET-запрос на поиск вещей: text={}", text);
        return itemService.searchItems(text);
    }
}