package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO newUserDTO);

    void deleteUserById(long id);

    UserDTO getUserById(long id);

    List<UserDTO> getAllUsers();

    boolean existsById(Long id);

    User validateUserExist(Long userId);
}
