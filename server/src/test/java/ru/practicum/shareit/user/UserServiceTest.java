package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
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
public class UserServiceTest {
    @Autowired
    UserService userService;

    UserDTO user1;
    UserDTO user2;

    @BeforeAll
    void beforeAll() {
        user1 = UserDTO.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = UserDTO.builder().name("Yandex2").email("yandex2@practicum.ru").build();
    }

    @Test
    void getAllUsers() {
        userService.createUser(user1);
        UserDTO newUser = userService.createUser(user2);
        List<UserDTO> users = userService.getAllUsers().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void createAndGetUser() {
        UserDTO user = userService.createUser(user1);
        UserDTO getUser = userService.getUserById(user.getId());

        assertThat(user.getId()).isEqualTo(getUser.getId());
        assertThat(user.getName()).isEqualTo(getUser.getName());
        assertThat(user.getEmail()).isEqualTo(getUser.getEmail());
    }


    @Test
    void updateUser() {
        UserDTO user = userService.createUser(user1);
        UserDTO updateUserDto = UserDTO.builder().name(user2.getName()).email(user2.getEmail()).build();
        UserDTO updateUser = userService.updateUser(user.getId(), updateUserDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user2.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    void updateUserNameIsNull() {
        UserDTO user = userService.createUser(user1);
        UserDTO userUpdateDto = UserDTO.builder().email("yandex2@practicum.ru").build();
        UserDTO updateUser = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user.getName());
        assertThat(updateUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }

    @Test
    void updateUserEmailIsNull() {
        UserDTO user = userService.createUser(user1);
        UserDTO userUpdateDto = UserDTO.builder().name("Yandex2").build();
        UserDTO updateUser = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void deleteUser() {
        UserDTO user = userService.createUser(user1);
        userService.deleteUserById(user.getId());

        assertThatThrownBy(() -> userService.getUserById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
