package ru.practicum.shareit.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.dto.booking.BookingState;
import ru.practicum.shareit.server.service.booking.BookingService;

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
    private BookingService bookingService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Поддержка Java 8 типов (LocalDateTime)

    // Тест для addBooking
    @Test
    void testAddBooking() throws Exception {
        Long userId = 1L;
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto expectedDto = BookingDto.builder()
                .id(1L)
                .start(createDto.getStart())
                .end(createDto.getEnd())
                .itemId(createDto.getItemId())
                .build();

        when(bookingService.addBooking(eq(userId), eq(createDto))).thenReturn(expectedDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId").value(expectedDto.getItemId()));

        verify(bookingService, times(1)).addBooking(eq(userId), eq(createDto));
    }

    // Тест для updateBooking
    @Test
    void testUpdateBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        Boolean approved = true;

        BookingDto expectedDto = BookingDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(2L)
                .build();

        when(bookingService.updateBooking(eq(userId), eq(bookingId), eq(approved))).thenReturn(expectedDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId").value(expectedDto.getItemId()));

        verify(bookingService, times(1)).updateBooking(eq(userId), eq(bookingId), eq(approved));
    }

    // Тест для getBookingById
    @Test
    void testGetBookingById() throws Exception {
        Long bookingId = 1L;

        BookingDto expectedDto = BookingDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(2L)
                .build();

        when(bookingService.getBookingById(eq(bookingId))).thenReturn(expectedDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.itemId").value(expectedDto.getItemId()));

        verify(bookingService, times(1)).getBookingById(eq(bookingId));
    }

    // Тест для getAllBookings
    @Test
    void testGetAllBookings() throws Exception {
        Long requesterId = 1L;
        BookingState state = BookingState.ALL;

        BookingDto booking1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(2L)
                .build();

        BookingDto booking2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(4))
                .itemId(3L)
                .build();

        List<BookingDto> expectedBookings = List.of(booking1, booking2);

        when(bookingService.getAllBookings(eq(state), eq(requesterId))).thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings")
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()))
                .andExpect(jsonPath("$[0].id").value(booking1.getId()))
                .andExpect(jsonPath("$[1].id").value(booking2.getId()));

        verify(bookingService, times(1)).getAllBookings(eq(state), eq(requesterId));
    }

    // Тест для getAllBookingsByUser
    @Test
    void testGetAllBookingsByUser() throws Exception {
        Long userId = 1L;
        Long requesterId = 1L;
        BookingState state = BookingState.ALL;

        BookingShortDto booking1 = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingShortDto booking2 = BookingShortDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(4))
                .build();

        List<BookingShortDto> expectedBookings = List.of(booking1, booking2);

        when(bookingService.getAllBookingsByUser(eq(userId), eq(state), eq(requesterId))).thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings/users/{userId}/bookings", userId)
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", requesterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()))
                .andExpect(jsonPath("$[0].id").value(booking1.getId()))
                .andExpect(jsonPath("$[1].id").value(booking2.getId()));

        verify(bookingService, times(1)).getAllBookingsByUser(eq(userId), eq(state), eq(requesterId));
    }

    // Тест для getAllBookingsForOwnerItems
    @Test
    void testGetAllBookingsForOwnerItems() throws Exception {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;

        BookingShortDto booking1 = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingShortDto booking2 = BookingShortDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(4))
                .build();

        List<BookingShortDto> expectedBookings = List.of(booking1, booking2);

        when(bookingService.getAllBookingsForOwnerItems(eq(ownerId), eq(state))).thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings/bookings/owner")
                        .param("state", state.name())
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedBookings.size()))
                .andExpect(jsonPath("$[0].id").value(booking1.getId()))
                .andExpect(jsonPath("$[1].id").value(booking2.getId()));

        verify(bookingService, times(1)).getAllBookingsForOwnerItems(eq(ownerId), eq(state));
    }
}