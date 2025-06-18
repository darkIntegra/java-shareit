package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.item.CommentDto;
import ru.practicum.shareit.gateway.dto.item.ItemDto;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для addItem
    @Test
    void testAddItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(1L);
        expectedItemDto.setName("Test Item");
        expectedItemDto.setDescription("Test Description");
        expectedItemDto.setAvailable(true);

        when(itemClient.addItem(eq(userId), eq(itemDto)))
                .thenReturn(ResponseEntity.status(201).body(expectedItemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemClient, times(1)).addItem(eq(userId), eq(itemDto));
    }

    // Тест для addComment
    @Test
    void testAddComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a test comment");

        CommentDto expectedCommentDto = new CommentDto();
        expectedCommentDto.setId(1L);
        expectedCommentDto.setText("This is a test comment");

        when(itemClient.addComment(eq(userId), eq(itemId), eq(commentDto)))
                .thenReturn(ResponseEntity.status(201).body(expectedCommentDto));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedCommentDto.getText()));

        verify(itemClient, times(1)).addComment(eq(userId), eq(itemId), eq(commentDto));
    }

    // Тест для updateItem
    @Test
    void testUpdateItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(false);

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName("Updated Item");
        expectedItemDto.setDescription("Updated Description");
        expectedItemDto.setAvailable(false);

        when(itemClient.updateItem(eq(userId), eq(itemId), eq(itemDto)))
                .thenReturn(ResponseEntity.ok(expectedItemDto));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemClient, times(1)).updateItem(eq(userId), eq(itemId), eq(itemDto));
    }

    // Тест для getItemById
    @Test
    void testGetItemById() throws Exception {
        Long itemId = 1L;

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName("Test Item");
        expectedItemDto.setDescription("Test Description");
        expectedItemDto.setAvailable(true);

        when(itemClient.getItemById(eq(itemId)))
                .thenReturn(ResponseEntity.ok(expectedItemDto));

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemClient, times(1)).getItemById(eq(itemId));
    }

    // Тест для getAllItems
    @Test
    void testGetAllItems() throws Exception {
        Long userId = 1L;

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemClient.getAllItems(eq(userId)))
                .thenReturn(ResponseEntity.ok(expectedItems));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemClient, times(1)).getAllItems(eq(userId));
    }

    // Тест для getItemsByOwnerId
    @Test
    void testGetItemsByOwnerId() throws Exception {
        Long ownerId = 1L;

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemClient.getItemsByOwnerId(eq(ownerId)))
                .thenReturn(ResponseEntity.ok(expectedItems));

        mockMvc.perform(get("/items/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemClient, times(1)).getItemsByOwnerId(eq(ownerId));
    }

    // Тест для deleteItemById
    @Test
    void testDeleteItemById() throws Exception {
        Long itemId = 1L;

        when(itemClient.deleteItemById(eq(itemId))).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isNoContent());

        verify(itemClient, times(1)).deleteItemById(eq(itemId));
    }

    // Тест для deleteAllItems
    @Test
    void testDeleteAllItems() throws Exception {
        when(itemClient.deleteAllItems()).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/items"))
                .andExpect(status().isNoContent());

        verify(itemClient, times(1)).deleteAllItems();
    }

    // Тест для searchItems
    @Test
    void testSearchItems() throws Exception {
        String text = "searchText";

        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemClient.searchItems(eq(text)))
                .thenReturn(ResponseEntity.ok(expectedItems));

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemClient, times(1)).searchItems(eq(text));
    }
}