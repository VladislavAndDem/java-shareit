package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.info("USV -> start process create User {}", userDTO);
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank() || !userDTO.getEmail().contains("@")) {
            String str = "Имейл должен быть указан, содержать символ '@'";
            log.error(str);
            throw new ValidationException(str);
        }
        checkFreeEmail(userDTO);

        return UserMapper.toUserDTO(userRepository.save(UserMapper.toUserWithoutId(userDTO)));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO newUserDTO) {
        log.info("US -> Процесс проверки данных перед обновлением");
        if (id == null) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан");
        }
        checkFreeEmail(newUserDTO);
        Optional<User> oldUserOpt = userRepository.findById(id);

        if (oldUserOpt.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User oldUser = oldUserOpt.get();
        User newUser = new User(oldUser.getId(), newUserDTO.getName(), newUserDTO.getEmail());
        if (newUserDTO.getEmail() != null && !checkEmail(newUser, userRepository.findAll())) {
            log.debug("Обновление имейла {}", newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUserDTO.getName() != null) {
            log.debug("Обновление имени {}", newUser.getName());
            oldUser.setName(newUser.getName());
        }
        log.info("Данный пользователя обновляются");

        return UserMapper.toUserDTO(userRepository.save(oldUser));
    }

    private boolean checkEmail(User user, Collection<User> userCollection) {
        return userCollection
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail())) // Фильтруем по email
                .anyMatch(u -> u.getId() != user.getId()); // Проверяем, что это не тот же самый пользователь
    }

    private boolean checkFreeEmail(UserDTO userDTO) {
        List<User> users = userRepository.findAll();

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
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserById(long id) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User user = userOptional.get();
        log.info("US -> пользователь с id {} найден", id);
        return UserMapper.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        boolean userBool = userRepository.existsById(id);
        if (!userBool) {
            throw new NotFoundException("Пользователь не существует");
        }
        return userBool;
    }
}
