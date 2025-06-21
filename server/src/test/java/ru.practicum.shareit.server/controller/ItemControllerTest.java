package ru.practicum.shareit.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.service.item.ItemService;

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
    private ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для addItem
    @Test
    void testAddItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.addItem(eq(userId), eq(itemDto))).thenReturn(expectedItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemService, times(1)).addItem(eq(userId), eq(itemDto));
    }

    // Тест для addComment
    @Test
    void testAddComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .text("This is a test comment")
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .id(1L)
                .text("This is a test comment")
                .build();

        when(itemService.addComment(eq(userId), eq(itemId), eq(commentDto))).thenReturn(expectedCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedCommentDto.getText()));

        verify(itemService, times(1)).addComment(eq(userId), eq(itemId), eq(commentDto));
    }

    // Тест для updateItem
    @Test
    void testUpdateItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemService.updateItem(eq(userId), eq(itemId), eq(itemDto))).thenReturn(expectedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemService, times(1)).updateItem(eq(userId), eq(itemId), eq(itemDto));
    }

    // Тест для getItemById
    @Test
    void testGetItemById() throws Exception {
        Long itemId = 1L;

        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.getItemById(eq(itemId))).thenReturn(expectedItemDto);

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedItemDto.getName()))
                .andExpect(jsonPath("$.description").value(expectedItemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItemDto.getAvailable()));

        verify(itemService, times(1)).getItemById(eq(itemId));
    }

    // Тест для getAllItems
    @Test
    void testGetAllItems() throws Exception {
        Long userId = 1L;

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemService.getAllItems(eq(userId))).thenReturn(expectedItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemService, times(1)).getAllItems(eq(userId));
    }

    // Тест для getItemsByOwnerId
    @Test
    void testGetItemsByOwnerId() throws Exception {
        Long ownerId = 1L;

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemService.getItemsByOwnerId(eq(ownerId))).thenReturn(expectedItems);

        mockMvc.perform(get("/items/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemService, times(1)).getItemsByOwnerId(eq(ownerId));
    }

    // Тест для deleteItemById
    @Test
    void testDeleteItemById() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItemById(eq(itemId));
    }

    // Тест для deleteAllItems
    @Test
    void testDeleteAllItems() throws Exception {
        mockMvc.perform(delete("/items"))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteAllItems();
    }

    // Тест для searchItems
    @Test
    void testSearchItems() throws Exception {
        String text = "searchText";

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        Collection<ItemDto> expectedItems = Arrays.asList(item1, item2);

        when(itemService.searchItems(eq(text))).thenReturn(expectedItems);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedItems.size()))
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()));

        verify(itemService, times(1)).searchItems(eq(text));
    }
}