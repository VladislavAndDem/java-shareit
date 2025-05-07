package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private long id;

    @Autowired
    public ItemServiceImpl(UserService userService, ItemStorage itemStorage, ItemMapper itemMapper) {
        this.userService = userService;
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("IS -> пришли параметры userId - {} и ItemDto - {}", userId, itemDto);
        if (userId == 0) {
            log.error("InternalServerException - не указан заголовок и его значение");
            throw new InternalServerException("Id должен быть указан");
        }
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            log.error("InternalServerException - name или description не может быть пустым");
            throw new InternalServerException("name или description должны быть указаны");
        }
        userService.getUserById(userId);

        Item item = itemMapper.toItem(itemDto);

        item.setId(++id);
        item.setOwner(userId);

        itemStorage.create(item);
        log.info("Item создан {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        log.info("IS -> пришли параметры на обновление item userId - {}, itemId {} и ItemDto - {}", userId, itemId, itemDto);
        if (userId == 0) {
            log.error("InternalServerException - не указан заголовок и его значение");
            throw new InternalServerException("Id должен быть указан");
        }
        ItemDto oldItemDto = getItemByIdFromUser(userId, itemId);
        Item item = itemMapper.toItem(oldItemDto);

        if (itemDto.getDescription() == null && itemDto.getAvailable() == null) {
            item.setName(itemDto.getName());
        } else if (itemDto.getName() == null && itemDto.getAvailable() == null) {
            item.setDescription(itemDto.getDescription());
        } else if (itemDto.getName() == null && itemDto.getDescription() == null) {
            item.setAvailable(itemDto.getAvailable());
        } else {
            item.setAvailable(Boolean.TRUE.equals(itemDto.getAvailable()));
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
        }

        item.setId(oldItemDto.getId());
        itemStorage.update(itemId, item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemByIdFromUser(long userId, long itemId) {
        Optional<Item> itemOptional = itemStorage.getItemById(itemId);
        UserDTO userDTO = userService.getUserById(userId);
        if (itemOptional.isEmpty()) {
            log.info("NotFoundException - item не найден");
            throw new NotFoundException("Item с id " + itemId + " не найден");
        }
        if (itemOptional.get().getOwner() != userDTO.getId()) {
            throw new NotFoundException("У item нет владельца");
        }

        return itemMapper.toItemDto(itemOptional.get());
    }

    @Override
    public List<ItemDto> getAllItemFromUser(long userId) {
        List<Item> items = itemStorage.getAllItems();
        return items.stream()
                .filter(item -> item.getOwner() == userId)
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findAllByText(long userId, String text) {
        log.info("Начался процесс поиска");
        List<Item> items = itemStorage.getAllItems();
        return items.stream()
                .filter(item -> item.getOwner() == userId)
                .filter(Item::isAvailable) // Проверка доступности вещи
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) // Поиск по тексту
                .map(itemMapper::toItemDto)
                .toList();
    }
}
