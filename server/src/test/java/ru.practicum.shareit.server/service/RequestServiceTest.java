package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.item.ItemRepository;
import ru.practicum.shareit.server.repository.request.RequestRepository;
import ru.practicum.shareit.server.repository.user.UserRepository;
import ru.practicum.shareit.server.service.request.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class RequestServiceTest {

    @Autowired
    private RequestService requestService;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private User requester;
    private Request request;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        requester = new User();
        requester.setId(1L);
        requester.setName("Requester");
        requester.setEmail("requester@example.com");

        // Создаем запрос
        request = new Request();
        request.setId(1L);
        request.setDescription("Need a bike");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void testCreateRequest() {
        // Arrange
        RequestDto requestDto = RequestDto.builder()
                .description("Need a bike")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request savedRequest = invocation.getArgument(0);
            savedRequest.setId(1L); // Устанавливаем ID для сохраненного запроса
            return savedRequest;
        });

        // Act
        RequestDto createdRequest = requestService.createRequest(1L, requestDto);

        // Assert
        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo(requestDto.getDescription());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void testGetAllRequestsByUser() {
        // Arrange
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<RequestDto> requests = requestService.getAllRequestsByUser(1L);

        // Assert
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a bike");
        verify(requestRepository, times(1)).findByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void testGetAllRequestsExcludingUser() {
        // Arrange
        when(requestRepository.findAllExcludingRequester(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        // Act
        List<RequestDto> requests = requestService.getAllRequestsExcludingUser(1L);

        // Assert
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a bike");
        verify(requestRepository, times(1)).findAllExcludingRequester(1L);
    }

    @Test
    void testGetRequestById() {
        // Arrange
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        // Act
        RequestDto requestDto = requestService.getRequestById(1L);

        // Assert
        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getDescription()).isEqualTo("Need a bike");
        verify(requestRepository, times(1)).findById(1L);
    }
}