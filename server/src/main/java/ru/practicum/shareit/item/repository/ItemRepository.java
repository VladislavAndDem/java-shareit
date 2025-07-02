package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) " +
            "LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> findAllByText(String text);

    //List<Item> findByRequestIdOrderByIdDesc(Long requestId);
    /*@Query("SELECT i FROM Item i WHERE i.requestId = :requestId ORDER BY i.id DESC")
    List<Item> findByRequestIdOrderByIdDesc(@Param("requestId") Long requestId);*/
    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId ORDER BY i.id DESC")
    List<Item> findByRequestIdOrderByIdDesc(@Param("requestId") Long requestId);
}
