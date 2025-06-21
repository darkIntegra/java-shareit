package ru.practicum.shareit.server.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.server.model.item.Item;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.model.user.User;
import ru.practicum.shareit.server.repository.item.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private Request request; // Добавляем Request
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        // Создаем владельца вещей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        // Создаем запрос (Request)
        request = new Request();
        request.setDescription("Need a bike");
        request.setCreated(LocalDateTime.now());
        request.setRequester(owner); // Связываем с пользователем
        entityManager.persist(request);

        // Создаем вещи
        item1 = new Item();
        item1.setName("Bike");
        item1.setDescription("Mountain bike for rent");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1.setRequestId(request.getId()); // Устанавливаем request_id
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Skateboard");
        item2.setDescription("Skateboard for kids");
        item2.setAvailable(false); // Недоступная вещь
        item2.setOwner(owner);
        entityManager.persist(item2);
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицы в обратном порядке (сначала зависимые таблицы)
        EntityManager em = entityManager.getEntityManager();
        em.createQuery("DELETE FROM Item").executeUpdate();
        em.createQuery("DELETE FROM Request").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
    }

    @Test
    void testSearch() {
        String text = "bike";
        List<Item> items = itemRepository.search(text);
        assertThat(items).hasSize(1);
        assertThat(items).contains(item1);
    }

    @Test
    void testFindByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(owner.getId());
        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
    }

    @Test
    void testFindByRequestId() {
        List<Item> items = itemRepository.findByRequestId(request.getId());
        assertThat(items).hasSize(1);
        assertThat(items).contains(item1);
    }
}