package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemByIdFromUser(long userId, long itemId);

    Item getItemOptionalById(long itemId);

    List<ItemDto> getAllItemFromUser(long userId);

    List<ItemDto> findAllByText(long userId, String text);

    CommentDto saveComment(long userId, long itemId, CommentDto commentDto);
}
