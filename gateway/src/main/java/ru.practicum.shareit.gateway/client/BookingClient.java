package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.dto.booking.BookingCreateDto;
import ru.practicum.shareit.gateway.dto.booking.BookingState;


import java.util.Map;

@Service

public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Long userId, BookingCreateDto createDto) {
        return post("", userId, null, createDto);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, null);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId) {
        String path = "/" + bookingId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> getAllBookings(BookingState state, Long requesterId) {
        String path = "";
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, requesterId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByUser(Long userId, BookingState state, Long requesterId) {
        String path = "/users/" + userId + "/bookings";
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, requesterId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsForOwnerItems(Long ownerId, BookingState state) {
        String path = "/bookings/owner";
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, ownerId, parameters);
    }
}