package ru.practicum.shareit.item.controler;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("IC -> запрос на создание item - {}", itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                          @PathVariable long itemId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("IC -> запрос на обновление item - {}", itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItemByIdFromUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                       @PathVariable long itemId) {
        log.info("IC -> запрос на получение item по id - {}", itemId);
        return itemService.getItemByIdFromUser(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemFromUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return itemService.getAllItemFromUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                    @RequestParam(defaultValue = "") String text) {
        log.info("IC -> запрос на поиск item по тексту {}", text);
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemService.findAllByText(userId, text);
        }
    }
}
