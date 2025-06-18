package ru.practicum.shareit.server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.model.item.Comment;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.item.CommentRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User author;
    private Item item;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        // Создаем автора комментария
        author = new User();
        author.setName("Author");
        author.setEmail("author@example.com");
        entityManager.persist(author);

        // Создаем вещь
        item = new Item();
        item.setName("Bike");
        item.setDescription("Mountain bike for rent");
        item.setAvailable(true);
        item.setOwner(author); // Связываем с владельцем
        entityManager.persist(item);

        // Создаем комментарии
        comment1 = new Comment();
        comment1.setText("Great bike!");
        comment1.setItem(item);
        comment1.setAuthor(author);
        comment1.setCreated(LocalDateTime.now());
        entityManager.persist(comment1);

        comment2 = new Comment();
        comment2.setText("Awesome experience!");
        comment2.setItem(item);
        comment2.setAuthor(author);
        comment2.setCreated(LocalDateTime.now());
        entityManager.persist(comment2);
    }

    @Test
    void testFindByItemId() {
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertThat(comments).hasSize(2);
        assertThat(comments).contains(comment1, comment2);
    }
}