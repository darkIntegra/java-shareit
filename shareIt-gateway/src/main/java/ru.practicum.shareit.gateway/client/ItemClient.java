package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.item.CommentDto;
import ru.practicum.shareit.gateway.dto.item.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    private static final String BASE_URL = "http://localhost:9090/items";

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        return post(BASE_URL, userId, null, itemDto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        String path = BASE_URL + "/" + itemId + "/comment";
        return post(path, userId, null, commentDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        String path = BASE_URL + "/" + itemId;
        return patch(path, userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        String path = BASE_URL + "/" + itemId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        String path = BASE_URL;
        Map<String, Object> parameters = Map.of("userId", userId);
        return get(path, null, parameters);
    }

    public ResponseEntity<Object> getItemsByOwnerId(Long ownerId) {
        String path = BASE_URL + "/owner/" + ownerId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> deleteItemById(Long itemId) {
        String path = BASE_URL + "/" + itemId;
        return delete(path, null, null);
    }

    public ResponseEntity<Object> deleteAllItems() {
        return delete(BASE_URL, null, null);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String path = BASE_URL + "/search";
        Map<String, Object> parameters = Map.of("text", text);
        return get(path, null, parameters);
    }
}