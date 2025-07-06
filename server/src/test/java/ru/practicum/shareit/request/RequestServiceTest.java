package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RequestServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    RequestService requestService;

    UserDTO user1;
    ItemDto item1;
    NewRequestDto itemRequest1;

    @BeforeAll
    void beforeAll() {
        user1 = UserDTO.builder().name("Yandex").email("yandex@practicum.ru").build();
        item1 = ItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
        itemRequest1 = NewRequestDto.builder().description("YandexPracticum").build();
    }

    @Test
    void createAndGetRequest() {
        UserDTO user = userService.createUser(user1);
        RequestDto requestDto = requestService.create(itemRequest1, user.getId());
        RequestDto getItemRequest = requestService.findById(user.getId(), requestDto.getId());

        assertThat(requestDto.getId()).isEqualTo(getItemRequest.getId());
        assertThat(requestDto.getDescription()).isEqualTo(getItemRequest.getDescription());
        assertThat(requestDto.getRequestorName()).isEqualTo(getItemRequest.getRequestorName());
    }

    @Test
    void throwExceptionWhenUserIsNotFound() {
        assertThatThrownBy(() -> requestService.create(itemRequest1, 1000L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> requestService.create(itemRequest1, null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void getItemRequestByRequestorId() {
        UserDTO user = userService.createUser(user1);
        RequestDto itemRequest = requestService.create(itemRequest1, user.getId());

        List<RequestDto> itemRequests = requestService.getAllRequestsById(user.getId()).stream()
                .toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequestorName()).isEqualTo(itemRequest.getRequestorName());
    }

    @Test
    void getAllRequests() {
        UserDTO user = userService.createUser(user1);
        RequestDto itemRequest = requestService.create(itemRequest1, user.getId());

        List<RequestDto> itemRequests = requestService.getAllRequestsById(user.getId()).stream().toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequestorName()).isEqualTo(itemRequest.getRequestorName());
    }
}