package ru.practicum.shareit.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.dto.booking.BookingCreateDto;
import ru.practicum.shareit.server.dto.booking.BookingDto;
import ru.practicum.shareit.server.dto.booking.BookingShortDto;
import ru.practicum.shareit.server.mapper.booking.BookingMapper;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.booking.BookingStatus;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void testToBookingDto() {
        // Arrange
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item Name");

        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        // Act
        BookingDto dto = BookingMapper.toBookingDto(booking);

        // Assert
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getStart()).isEqualTo(booking.getStartDate());
        assertThat(dto.getEnd()).isEqualTo(booking.getEndDate());
        assertThat(dto.getItemId()).isEqualTo(booking.getItem().getId());
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(dto.getBooker().getId()).isEqualTo(booking.getBooker().getId());
        assertThat(dto.getItem().getId()).isEqualTo(booking.getItem().getId());
        assertThat(dto.getItem().getName()).isEqualTo(booking.getItem().getName());
    }

    @Test
    void testToBookingShortDto() {
        // Arrange
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item Name");

        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        // Act
        BookingShortDto shortDto = BookingMapper.toBookingShortDto(booking);

        // Assert
        assertThat(shortDto.getId()).isEqualTo(booking.getId());
        assertThat(shortDto.getStart()).isEqualTo(booking.getStartDate());
        assertThat(shortDto.getEnd()).isEqualTo(booking.getEndDate());
        assertThat(shortDto.getBookerId()).isEqualTo(booking.getBooker().getId());
    }

    @Test
    void testToBookingFromBookingDto() {
        // Arrange
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(2L)
                .status(BookingStatus.APPROVED)
                .build();

        // Act
        Booking booking = BookingMapper.toBooking(dto);

        // Assert
        assertThat(booking.getId()).isEqualTo(dto.getId());
        assertThat(booking.getStartDate()).isEqualTo(dto.getStart());
        assertThat(booking.getEndDate()).isEqualTo(dto.getEnd());
    }

    @Test
    void testToBookingFromBookingShortDto() {
        // Arrange
        BookingShortDto shortDto = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .bookerId(2L)
                .build();

        // Act
        Booking booking = BookingMapper.toBooking(shortDto);

        // Assert
        assertThat(booking.getStartDate()).isEqualTo(shortDto.getStart());
        assertThat(booking.getEndDate()).isEqualTo(shortDto.getEnd());
    }

    @Test
    void testToBookingFromBookingCreateDto() {
        // Arrange
        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(2L)
                .build();

        Item item = new Item();
        item.setId(2L);
        item.setName("Item Name");

        User booker = new User();
        booker.setId(1L);

        // Act
        Booking booking = BookingMapper.toBooking(createDto, item, booker);

        // Assert
        assertThat(booking.getStartDate()).isEqualTo(createDto.getStart());
        assertThat(booking.getEndDate()).isEqualTo(createDto.getEnd());
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}