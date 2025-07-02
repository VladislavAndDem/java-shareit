package ru.practicum.shareit.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserDTO userDTO) {
        log.info("UC -> запрос на создание пользователя {}", userDTO);
        return userService.createUser(userDTO);
    }

    @PatchMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable("userId") long id, @RequestBody UserDTO userDTO) {
        log.info("UC -> Запрос на обновление пользователя {}", userDTO);
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") long id) {
        userService.deleteUserById(id);
    }

    @GetMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable("userId") long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

}
