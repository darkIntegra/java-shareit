package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody @Validated(OnCreate.class) ItemDto dto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(userId, dto);
    }

    // Добавление комментария
    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        // Логируем входящий запрос
        log.info("Получен запрос на добавление комментария: itemId={}, userId={}, commentDto={}", itemId, userId, commentDto);
        // Вызываем метод сервиса
        CommentDto result = itemService.addComment(userId, itemId, commentDto);
        // Логируем успешное завершение операции
        log.info("Комментарий успешно добавлен: itemId={}, userId={}, commentId={}", itemId, userId, result.getId());

        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody @Validated(OnUpdate.class) ItemDto dto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/owner/{ownerId}")
    public Collection<ItemDto> getItemsByOwnerId(@PathVariable Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable Long itemId) {
        itemService.deleteItemById(itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllItems() {
        itemService.deleteAllItems();
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }
}