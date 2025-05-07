package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        log.info("UC -> запрос на создание пользователя {}", userDTO);
        return userService.createUser(userDTO);
    }

    @PatchMapping(value = "/{userId}")
    public UserDTO update(@PathVariable("userId") long id, @Valid @RequestBody UserDTO userDTO) {
        log.info("UC -> Запрос на обновление пользователя {}", userDTO);
        return userService.updateUser(id, userDTO);
    }

    @DeleteMapping(value = "/{userId}")
    public void delete(@PathVariable("userId") long id) {
        userService.deleteUserById(id);
    }

    @GetMapping(value = "/{userId}")
    public UserDTO getUserById(@PathVariable("userId") long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

}
