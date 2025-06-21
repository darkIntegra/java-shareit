package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.booking.BookingCreateDto;
import ru.practicum.shareit.gateway.dto.booking.BookingDto;
import ru.practicum.shareit.gateway.dto.booking.BookingShortDto;
import ru.practicum.shareit.gateway.dto.booking.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для addBooking
    @Test
    void testAddBooking() throws Exception {
        Long userId = 1L;

        // Создаем JSON-запрос с динамическими датами
        LocalDateTime now = LocalDateTime.now();
        String jsonRequest = """
            {
                "itemId": 1,
                "start": "%s",
                "end": "%s"
            }
            """.formatted(
                now.plusHours(1).toString(), // start — через 1 час
                now.plusHours(2).toString() // end — через 2 часа
        );

        BookingDto expectedBookingDto = new BookingDto();
        expectedBookingDto.setId(1L);

        // Мокируем ответ от bookingClient
        when(bookingClient.addBooking(eq(userId), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.status(201).body(expectedBookingDto));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedBookingDto.getId()));

        verify(bookingClient, times(1)).addBooking(eq(userId), any(BookingCreateDto.class));
    }

    // Тест для updateBooking
    @Test
    void testUpdateBooking() throws Exception {
        Long bookingId = 1L;
        Boolean approved = true;
        Long userId = 1L;
        BookingDto expectedBookingDto = new BookingDto();
        expectedBookingDto.setId(bookingId);

        when(bookingClient.updateBooking(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(ResponseEntity.ok(expectedBookingDto));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBookingDto.getId()));

        verify(bookingClient, times(1)).updateBooking(eq(userId), eq(bookingId), eq(approved));
    }

    // Тест для getBookingById
    @Test
    void testGetBookingById() throws Exception {
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();
        expectedBookingDto.setId(bookingId);

        when(bookingClient.getBookingById(eq(bookingId)))
                .thenReturn(ResponseEntity.ok(expectedBookingDto));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBookingDto.getId()));

        verify(bookingClient, times(1)).getBookingById(eq(bookingId));
    }

    // Тест для getAllBookings
    @Test
    void testGetAllBookings() throws Exception {
        BookingState state = BookingState.ALL;
        Long requesterId = 1L;
        List<BookingDto> expectedBookings = List.of(new BookingDto(), new BookingDto());

        when(bookingClient.getAllBookings(eq(state), eq(requesterId)))
                .thenReturn(ResponseEntity.ok(expectedBookings));

        mockMvc.perform(get("/bookings")
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()));

        verify(bookingClient, times(1)).getAllBookings(eq(state), eq(requesterId));
    }

    // Тест для getAllBookingsByUser
    @Test
    void testGetAllBookingsByUser() throws Exception {
        Long userId = 1L;
        Long requesterId = 1L;
        BookingState state = BookingState.ALL;
        List<BookingShortDto> expectedBookings = List.of(new BookingShortDto(), new BookingShortDto());

        when(bookingClient.getAllBookingsByUser(eq(userId), eq(state), eq(requesterId)))
                .thenReturn(ResponseEntity.ok(expectedBookings));

        mockMvc.perform(get("/bookings/users/{userId}/bookings", userId)
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()));

        verify(bookingClient, times(1)).getAllBookingsByUser(eq(userId), eq(state), eq(requesterId));
    }

    // Тест для getAllBookingsForOwnerItems
    @Test
    void testGetAllBookingsForOwnerItems() throws Exception {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        List<BookingShortDto> expectedBookings = List.of(new BookingShortDto(), new BookingShortDto());

        when(bookingClient.getAllBookingsForOwnerItems(eq(ownerId), eq(state)))
                .thenReturn(ResponseEntity.ok(expectedBookings));

        mockMvc.perform(get("/bookings/bookings/owner")
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()));

        verify(bookingClient, times(1)).getAllBookingsForOwnerItems(eq(ownerId), eq(state));
    }
}