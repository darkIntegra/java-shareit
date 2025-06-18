package ru.practicum.shareit.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.service.request.RequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для createRequest
    @Test
    void testCreateRequest() throws Exception {
        Long userId = 1L;
        RequestDto requestDto = RequestDto.builder()
                .description("Test Request")
                .build();

        RequestDto createdRequest = RequestDto.builder()
                .id(1L)
                .description("Test Request")
                .build();

        when(requestService.createRequest(eq(userId), eq(requestDto))).thenReturn(createdRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdRequest.getId()))
                .andExpect(jsonPath("$.description").value(createdRequest.getDescription()));

        verify(requestService, times(1)).createRequest(eq(userId), eq(requestDto));
    }

    // Тест для getAllRequestsByUser
    @Test
    void testGetAllRequestsByUser() throws Exception {
        Long userId = 1L;

        RequestDto request1 = RequestDto.builder()
                .id(1L)
                .description("Request 1")
                .build();

        RequestDto request2 = RequestDto.builder()
                .id(2L)
                .description("Request 2")
                .build();

        List<RequestDto> expectedRequests = Arrays.asList(request1, request2);

        when(requestService.getAllRequestsByUser(eq(userId))).thenReturn(expectedRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedRequests.size()))
                .andExpect(jsonPath("$[0].id").value(request1.getId()))
                .andExpect(jsonPath("$[0].description").value(request1.getDescription()))
                .andExpect(jsonPath("$[1].id").value(request2.getId()))
                .andExpect(jsonPath("$[1].description").value(request2.getDescription()));

        verify(requestService, times(1)).getAllRequestsByUser(eq(userId));
    }

    // Тест для getAllRequestsExcludingUser
    @Test
    void testGetAllRequestsExcludingUser() throws Exception {
        Long userId = 1L;

        RequestDto request1 = RequestDto.builder()
                .id(1L)
                .description("Request 1")
                .build();

        RequestDto request2 = RequestDto.builder()
                .id(2L)
                .description("Request 2")
                .build();

        List<RequestDto> expectedRequests = Arrays.asList(request1, request2);

        when(requestService.getAllRequestsExcludingUser(eq(userId))).thenReturn(expectedRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedRequests.size()))
                .andExpect(jsonPath("$[0].id").value(request1.getId()))
                .andExpect(jsonPath("$[0].description").value(request1.getDescription()))
                .andExpect(jsonPath("$[1].id").value(request2.getId()))
                .andExpect(jsonPath("$[1].description").value(request2.getDescription()));

        verify(requestService, times(1)).getAllRequestsExcludingUser(eq(userId));
    }

    // Тест для getRequestById
    @Test
    void testGetRequestById() throws Exception {
        Long requestId = 1L;

        RequestDto expectedRequest = RequestDto.builder()
                .id(requestId)
                .description("Test Request")
                .build();

        when(requestService.getRequestById(eq(requestId))).thenReturn(expectedRequest);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedRequest.getId()))
                .andExpect(jsonPath("$.description").value(expectedRequest.getDescription()));

        verify(requestService, times(1)).getRequestById(eq(requestId));
    }
}