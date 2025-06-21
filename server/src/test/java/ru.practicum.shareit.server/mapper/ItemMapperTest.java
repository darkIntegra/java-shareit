package ru.practicum.shareit.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.dto.item.ItemDto;
import ru.practicum.shareit.server.mapper.item.ItemMapper;
import ru.practicum.shareit.server.model.booking.Booking;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void testToItemDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(2L);

        // Act
        ItemDto dto = ItemMapper.toItemDto(item);

        // Assert
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getRequestId()).isEqualTo(item.getRequestId());
    }

    @Test
    void testToItem() {
        // Arrange
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item Name");
        dto.setDescription("Item Description");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        // Act
        Item item = ItemMapper.toItem(dto);

        // Assert
        assertThat(item.getId()).isEqualTo(dto.getId());
        assertThat(item.getName()).isEqualTo(dto.getName());
        assertThat(item.getDescription()).isEqualTo(dto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(dto.getAvailable());
        assertThat(item.getRequestId()).isEqualTo(dto.getRequestId());
    }

    @Test
    void testToItemDtoWithBookings() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(2L);

        Booking lastBooking = createBooking(1L, 3L);
        Booking nextBooking = createBooking(2L, 4L);

        // Act
        ItemDto dto = ItemMapper.toItemDtoWithBookings(item, lastBooking, nextBooking);

        // Assert
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getRequestId()).isEqualTo(item.getRequestId());

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(dto.getLastBooking().getStart()).isEqualTo(lastBooking.getStartDate());
        assertThat(dto.getLastBooking().getEnd()).isEqualTo(lastBooking.getEndDate());
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());

        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(dto.getNextBooking().getStart()).isEqualTo(nextBooking.getStartDate());
        assertThat(dto.getNextBooking().getEnd()).isEqualTo(nextBooking.getEndDate());
        assertThat(dto.getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
    }

    @Test
    void testToItemDtoWithComments() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequestId(2L);

        CommentDto comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("Great item!");

        CommentDto comment2 = new CommentDto();
        comment2.setId(2L);
        comment2.setText("Awesome!");

        List<CommentDto> comments = List.of(comment1, comment2);

        // Act
        ItemDto dto = ItemMapper.toItemDtoWithComments(item, comments);

        // Assert
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getRequestId()).isEqualTo(item.getRequestId());

        assertThat(dto.getComments()).hasSize(2);
        assertThat(dto.getComments().get(0).getId()).isEqualTo(comment1.getId());
        assertThat(dto.getComments().get(0).getText()).isEqualTo(comment1.getText());
        assertThat(dto.getComments().get(1).getId()).isEqualTo(comment2.getId());
        assertThat(dto.getComments().get(1).getText()).isEqualTo(comment2.getText());
    }

    private Booking createBooking(Long id, Long bookerId) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusHours(1));

        User booker = new User();
        booker.setId(bookerId);
        booking.setBooker(booker);

        return booking;
    }
}