package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemByIdFromUser(long userId, long itemId);

    List<ItemDto> getAllItemFromUser(long userId);

    List<ItemDto> findAllByText(long userId, String text);
}
