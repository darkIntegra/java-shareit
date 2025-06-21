package ru.practicum.shareit.server.repository.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.model.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Поиск вещей по тексту в названии или описании
    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> search(@Param("text") String text);

    // Поиск вещей, принадлежащих конкретному пользователю
    List<Item> findByOwnerId(Long ownerId);

    // Поиск вещей по requestId
    List<Item> findByRequestId(Long requestId);
}