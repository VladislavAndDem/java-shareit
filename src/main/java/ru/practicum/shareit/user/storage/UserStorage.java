package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(Long id, User user);

    void deleteUserById(Long id);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers();

}
