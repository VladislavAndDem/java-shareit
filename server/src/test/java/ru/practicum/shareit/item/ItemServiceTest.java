package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    RequestService requestService;

    UserDTO user1;
    UserDTO user2;
    ItemDto item1;
    ItemDto item2;

    @BeforeAll
    void beforeAll() {
        user1 = UserDTO.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = UserDTO.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = ItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        item2 = ItemDto.builder().name("Yandex2").description("YandexPracticum2").available(true).build();
    }

    @Test
    void create() {
        UserDTO user3 = userService.createUser(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto getItem = itemService.getItemByIdFromUser(user3.getId(), item.getId());

        assertThat(item.getId()).isEqualTo(getItem.getId());
        assertThat(item.getName()).isEqualTo(getItem.getName());
        assertThat(item.getDescription()).isEqualTo(getItem.getDescription());
        assertThat(item.getAvailable()).isEqualTo(getItem.getAvailable());
        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void createItemWithRequest() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        NewRequestDto itemRequestDto = NewRequestDto.builder().description("Yandex").build();
        RequestDto itemRequest = requestService.create(itemRequestDto, user4.getId());
        ItemDto item3 = ItemDto.builder()
                .name("Yandex2")
                .description("YandexPracticum2")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
        ItemDto item = itemService.create(user3.getId(), item3);
        ItemDto getItem = itemService.getItemByIdFromUser(user3.getId(), item.getId());
        assertThat(item.getId()).isEqualTo(getItem.getId());
        assertThat(item.getName()).isEqualTo(getItem.getName());
        assertThat(item.getDescription()).isEqualTo(getItem.getDescription());
        assertThat(item.getAvailable()).isEqualTo(getItem.getAvailable());
        assertThat(item.getRequestId()).isEqualTo(getItem.getRequestId());
    }

    @Test
    void updateItem() {
        ItemDto itemUpd = ItemDto.builder().name("Yandex2").description("YandexPracticum2").available(true).build();
        UserDTO user3 = userService.createUser(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), itemUpd);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item2.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item2.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item2.getAvailable());
    }

    @Test
    void updateItemNameIsNull() {
        UserDTO user3 = userService.createUser(user1);
        ItemDto item3 = ItemDto.builder().description("YandexPracticum2").available(false).build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item1.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item3.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item3.getAvailable());
    }

    @Test
    void updateItemDescriptionIsNull() {
        UserDTO user3 = userService.createUser(user1);
        ItemDto item3 = ItemDto.builder().name("Yandex2").available(false).build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item3.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item1.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item3.getAvailable());
    }

    @Test
    void updateItemAvailableIsNull() {
        UserDTO user3 = userService.createUser(user1);
        ItemDto item3 = ItemDto.builder().name("Yandex2").description("YandexPracticum2").build();
        ItemDto item = itemService.create(user3.getId(), item1);
        ItemDto updateItem = itemService.update(user3.getId(), item.getId(), item3);

        assertThat(updateItem.getId()).isEqualTo(item.getId());
        assertThat(updateItem.getName()).isEqualTo(item3.getName());
        assertThat(updateItem.getDescription()).isEqualTo(item3.getDescription());
        assertThat(updateItem.getAvailable()).isEqualTo(item1.getAvailable());
    }

    /*@Test
    void throwExceptionWhenUserIsNotOwnerWhenUpdateItem() {
        ItemDto itemUpd = ItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);

        assertThatThrownBy(() -> itemService.update(user4.getId(), item.getId(), itemUpd))
                .isInstanceOf(ValidationException.class);
    }*/

    @Test
    void getItemsByOwner() {
        UserDTO user3 = userService.createUser(user1);
        itemService.create(user3.getId(), item1);
        ItemDto item = itemService.create(user3.getId(), item2);

        List<ItemDto> items = itemService.getAllItemFromUser(user3.getId()).stream().toList();

        assertThat(items).hasSize(2);
        assertThat(items.get(1).getId()).isEqualTo(item.getId());
        assertThat(items.get(1).getName()).isEqualTo(item.getName());
        assertThat(items.get(1).getDescription()).isEqualTo(item.getDescription());
        assertThat(items.get(1).getAvailable()).isEqualTo(item.getAvailable());
    }

    /*@Test
    void addCommentToItem() {
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user4.getId(), item1);

        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2025, 3, 10, 12, 0))
                .end(LocalDateTime.of(2025, 3, 10, 12, 0).plusNanos(1))
                .build();
        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        bookingService.approve(user4.getId(), newBooking.getId(), true);

        CommentDto comment = CommentDto.builder().text("Text").build();
        CommentDto newComment = itemService.saveComment(user4.getId(), item.getId(), comment);

        assertThat(newComment.getText()).isEqualTo(comment.getText());
        assertThat(newComment.getAuthorName()).isEqualTo(user4.getName());
    }*/
}