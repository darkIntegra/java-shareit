package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.item.CommentDto;
import ru.practicum.shareit.gateway.dto.item.ItemDto;
import ru.practicum.shareit.gateway.validation.OnCreate;
import ru.practicum.shareit.gateway.validation.OnUpdate;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(
            @RequestBody @Validated(OnCreate.class) ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST-запрос на добавление вещи: {}, userId={}", dto, userId);
        return itemClient.addItem(userId, dto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST-запрос на добавление комментария: itemId={}, comment={}, userId={}", itemId, commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable Long itemId,
            @RequestBody @Validated(OnUpdate.class) ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH-запрос на обновление вещи: itemId={}, item={}, userId={}", itemId, dto, userId);
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        log.info("GET-запрос на получение вещи: itemId={}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET-запрос на получение всех вещей пользователя: userId={}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Object> getItemsByOwnerId(@PathVariable Long ownerId) {
        log.info("GET-запрос на получение вещей владельца: ownerId={}", ownerId);
        return itemClient.getItemsByOwnerId(ownerId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteItemById(@PathVariable Long itemId) {
        log.info("DELETE-запрос на удаление вещи: itemId={}", itemId);
        return itemClient.deleteItemById(itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteAllItems() {
        log.info("DELETE-запрос на удаление всех вещей");
        return itemClient.deleteAllItems();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        log.info("GET-запрос на поиск вещей: text={}", text);
        return itemClient.searchItems(text);
    }
}