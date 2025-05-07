package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private long id = 0;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("USV -> createUser {}", userDTO);
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank() || !userDTO.getEmail().contains("@")) {
            String str = "Имейл должен быть указан, содержать символ '@'";
            log.error(str);
            throw new ValidationException(str);
        }
        checkFreeEmail(userDTO);
        userDTO.setId(++id);
        log.info("Инициализация id = {}", userDTO.getId());
        return userMapper.toUserDTO(userStorage.create(userMapper.toUser(userDTO)));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO newUserDTO) {
        log.info("US -> Процесс проверки данных перед обновлением");
        if (id == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        checkFreeEmail(newUserDTO);
        UserDTO oldUserDTO = getUserById(id);
        User newUser = new User(id, newUserDTO.getName(), newUserDTO.getEmail());
        if (newUser.getEmail() != null && !checkEmail(newUser, userStorage.getAllUsers())) {
            log.debug("Обновление имейла {}", newUser.getEmail());
            oldUserDTO.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            log.debug("Обновление имени {}", newUser.getName());
            oldUserDTO.setName(newUser.getName());
        }
        log.info("Данный пользователя обновляются");
        return userMapper.toUserDTO(userStorage.update(id, userMapper.toUser(oldUserDTO)));
    }

    private boolean checkEmail(User user, Collection<User> userCollection) {

        return userCollection
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail())) // Фильтруем по email
                .anyMatch(u -> u.getId() != user.getId()); // Проверяем, что это не тот же самый пользователь
    }

    private boolean checkFreeEmail(UserDTO userDTO) {
        List<User> users = userStorage.getAllUsers();

        boolean emailExists = users.stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(userDTO.getEmail()));

        if (emailExists) {
            String str = "Имейл занят, подберите другой";
            log.error(str);
            throw new DuplicateException(str);
        } else {
            return emailExists;
        }

    }

    @Override
    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public UserDTO getUserById(long id) {

        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User user = userOptional.get();
        log.info("US -> пользователь с id {} найден", id);
        return userMapper.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        return users.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}
