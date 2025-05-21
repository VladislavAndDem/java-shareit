package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Item update(long itemId, Item item);

    Optional<Item> getItemById(long id);

    void deleteItemById(long id);

    List<Item> getAllItems();
}
