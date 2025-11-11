package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .requestId(2L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = "{ " +
                "\"id\": 1, " +
                "\"name\": \"ItemName\", " +
                "\"description\": \"ItemDescription\", " +
                "\"available\": true, " +
                "\"lastBooking\": null, " +
                "\"nextBooking\": null, " +
                "\"comments\": [], " +
                "\"requestId\": 2 " +
                "}";

        ItemDto itemDto = json.parse(jsonString).getObject();

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("ItemName");
        assertThat(itemDto.getDescription()).isEqualTo("ItemDescription");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isEmpty();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
    }
}