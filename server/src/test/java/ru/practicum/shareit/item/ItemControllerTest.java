package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;
    final ItemDto newItemDto = ItemDto.builder().name("name").available(false)
            .description("description").requestId(2L).build();

    final ItemDto updateItemDto = ItemDto.builder().id(1L).available(true)
            .description("newDescription").requestId(2L).build();

    final CommentDto commentDto = CommentDto.builder().id(1L).text("comment").authorName("userName")
            .created(LocalDateTime.of(2025, 5, 28, 21, 38, 0)).build();

    final ItemDto itemDto = ItemDto.builder().id(1L).name("name").available(false)
            .description("description").comments(List.of(commentDto)).requestId(2L).build();

    final ItemDto itemDto2 = ItemDto.builder().id(2L).name("name2").available(false)
            .description("description2").build();

    final ItemDto itemDtoAfterUpdate = ItemDto.builder().id(1L).name("eman").available(true)
            .description("newDescription").comments(List.of(commentDto)).requestId(2L).build();

    final CommentDto newCommentDto = CommentDto.builder().text("comment").build();

    final Long userId = 3L;
    final Long itemId = 1L;

    /*@Test
    void create() {
        when(itemService.create(userId, newItemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void update() {
        when(itemService.update(userId, itemId, updateItemDto))
                .thenReturn(itemDtoAfterUpdate);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoAfterUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoAfterUpdate.getName()))
                .andExpect(jsonPath("$.available").value(itemDtoAfterUpdate.getAvailable()))
                .andExpect(jsonPath("$.description").value(itemDtoAfterUpdate.getDescription()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$.comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$.comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.requestId").value(itemDtoAfterUpdate.getRequestId()));
    }

    @Test
    void getItemByIdFromUser() {
        when(itemService.getItemByIdFromUser(userId, itemId))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$.comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$.comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void getAllItemFromUser() {
        when(itemService.getAllItemFromUser(userId)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].comments[0].text").value(commentDto.getText()))
                .andExpect(jsonPath("$[0].comments[0].authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto.getRequestId()));
    }

    @Test
    void searchItem() {
        String searchText = "non-existent";
        when(itemService.findAllByText(searchText))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void saveComment() {
        when(itemService.saveComment(userId, itemId, newCommentDto))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(newCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value("2025-05-28T21:38:00"));
    }*/
}