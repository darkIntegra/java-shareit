package ru.practicum.shareit.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.dto.item.RequestItemDto;
import ru.practicum.shareit.server.dto.request.RequestDto;
import ru.practicum.shareit.server.mapper.request.RequestMapper;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMapperTest {

    @Test
    void testToRequestDto_WithoutItems() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Test Request");
        request.setCreated(LocalDateTime.now());

        RequestDto dto = RequestMapper.toRequestDto(request);

        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getCreated()).isEqualTo(request.getCreated());
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testToRequestDto_WithItems() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Test Request");
        request.setCreated(LocalDateTime.now());

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Item 1");

        User owner = new User();
        owner.setId(3L);
        item1.setOwner(owner);

        List<Item> items = List.of(item1);

        RequestDto dto = RequestMapper.toRequestDto(request, items);

        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getCreated()).isEqualTo(request.getCreated());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(item1.getId());
        assertThat(dto.getItems().get(0).getName()).isEqualTo(item1.getName());
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void testToRequest() {
        RequestDto dto = new RequestDto();
        dto.setDescription("Test Request");

        Request request = RequestMapper.toRequest(dto);

        assertThat(request.getDescription()).isEqualTo(dto.getDescription());
        assertThat(request.getCreated()).isNotNull();
    }

    @Test
    void testToRequestItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");

        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        RequestItemDto itemDto = RequestMapper.toRequestItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getOwnerId()).isEqualTo(owner.getId());
    }
}