package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        log.info("IMUS -> request to create {}", user);
        users.put(user.getId(), user);
        log.info("IMUS -> user inserted to map {}", users.get(user.getId()));
        return user;
    }

    @Override
    public User update(Long id, User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }
}
