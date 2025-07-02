package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@RestController
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ItemController {
    ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.findAllOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByNameOrDescription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(value = "text", required = false) String text) {
        return itemClient.findItemsByNameOrDescription(userId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable("itemId") Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody NewItemDto newItemDto) {
        return itemClient.createItem(userId, newItemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody UpdateItemDto updateItemDto) {
        return itemClient.updateItem(userId, itemId, updateItemDto);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody NewCommentDto newCommentDto) {
        return itemClient.addComment(userId, itemId, newCommentDto);
    }
}