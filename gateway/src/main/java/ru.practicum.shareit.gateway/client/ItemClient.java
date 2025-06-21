package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.dto.item.CommentDto;
import ru.practicum.shareit.gateway.dto.item.ItemDto;

import java.util.Map;


@Service

public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        return post("", userId, null, itemDto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        String path = "/" + itemId + "/comment";
        return post(path, userId, null, commentDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        String path = "/" + itemId;
        return patch(path, userId, null, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        String path = "/" + itemId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        Map<String, Object> parameters = Map.of("userId", userId);
        return get("", null, parameters);
    }

    public ResponseEntity<Object> getItemsByOwnerId(Long ownerId) {
        String path = "/owner/" + ownerId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> deleteItemById(Long itemId) {
        String path = "/" + itemId;
        return delete(path, null, null);
    }

    public ResponseEntity<Object> deleteAllItems() {
        return delete("", null, null);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String path = "/search";
        Map<String, Object> parameters = Map.of("text", text);
        return get(path, null, parameters);
    }
}