package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        log.info("IMIS -> поступил объект Item - {}", item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long itemId, Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void deleteItemById(long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAllItems() {
        return items.values().stream().toList();
    }
}
