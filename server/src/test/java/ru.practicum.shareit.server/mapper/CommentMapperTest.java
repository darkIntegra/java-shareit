package ru.practicum.shareit.server.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.dto.item.CommentDto;
import ru.practicum.shareit.server.mapper.item.CommentMapper;
import ru.practicum.shareit.server.model.item.Comment;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void testToComment() {
        // Arrange
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Item item = new Item();
        item.setId(1L);

        User author = new User();
        author.setId(2L);
        author.setName("John Doe");

        // Act
        Comment comment = CommentMapper.toComment(commentDto, item, author);

        // Assert
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull(); // Проверяем, что дата установлена
    }

    @Test
    void testToCommentDto() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setCreated(LocalDateTime.now());

        User author = new User();
        author.setId(2L);
        author.setName("John Doe");
        comment.setAuthor(author);

        // Act
        CommentDto dto = CommentMapper.toCommentDto(comment);

        // Assert
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(dto.getCreated()).isEqualTo(comment.getCreated());
    }
}