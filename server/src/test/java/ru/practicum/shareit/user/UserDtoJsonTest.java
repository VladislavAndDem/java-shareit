package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserDTO> json;

    @Test
    void testSerialize() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setName("name");
        dto.setEmail("email@email.ru");

        JsonContent<UserDTO> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = "{ \"id\": 1, \"name\": \"name\", \"email\": \"email@email.ru\" }";

        UserDTO dto = json.parse(jsonString).getObject();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("name");
        assertThat(dto.getEmail()).isEqualTo("email@email.ru");
    }
}