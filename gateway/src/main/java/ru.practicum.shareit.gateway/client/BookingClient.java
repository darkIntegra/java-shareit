package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.booking.BookingCreateDto;
import ru.practicum.shareit.gateway.dto.booking.BookingState;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

    private static final String BASE_URL = "http://localhost:9090/bookings";

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> addBooking(Long userId, BookingCreateDto createDto) {
        return post(BASE_URL, userId, null, createDto);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        String path = BASE_URL + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, null);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId) {
        String path = BASE_URL + "/" + bookingId;
        return get(path, null, null);
    }

    public ResponseEntity<Object> getAllBookings(BookingState state, Long requesterId) {
        String path = BASE_URL;
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, requesterId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsByUser(Long userId, BookingState state, Long requesterId) {
        String path = BASE_URL + "/users/" + userId + "/bookings";
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, requesterId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsForOwnerItems(Long ownerId, BookingState state) {
        String path = BASE_URL + "/bookings/owner";
        Map<String, Object> parameters = Map.of("state", state);
        return get(path, ownerId, parameters);
    }
}