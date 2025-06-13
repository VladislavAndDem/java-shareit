package ru.practicum.shareit.item.controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

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
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemByIdFromUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                       @PathVariable long itemId) {
        log.info("IC -> запрос на получение item по id - {}", Optional.of(itemId));
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

        return itemService.findAllByText(userId, text);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                  @PathVariable long itemId,
                                  @RequestBody CommentDto commentDto) {
        return itemService.saveComment(userId, itemId, commentDto);
    }

    /*@GetMapping(value = "/{itemId}")
    public List<CommentDto> findAllCommentByItemId(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                                   @PathVariable long itemId) {
        return itemService.findAllCommentByItemId(userId, itemId);*/

}
