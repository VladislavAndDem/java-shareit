package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService,
                              BookingMapper bookingMapper, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(Long userId, NewBookingRequest request) {
        // Получаем товар, который хотим забронировать
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", request.getItemId());
                    return new NotFoundException("Вещь с id " + request.getItemId() + " не найдена");
                });
        ItemDto itemDto = ItemMapper.toItemDto(item);
        UserDTO userDto = UserMapper.toUserDTO(booker);

        // Проверяем доступен ли товар к бронированию
        if (!itemDto.getAvailable()) {
            throw new InternalServerException("Вещь недоступна для бронирования");
        }
        // Проверяем коректность дат старта и завершения бронирования
        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();
        LocalDateTime today = LocalDateTime.now();
        if (start.isAfter(end) && start.isBefore(today)) {
            throw new InternalServerException("Неправильное время");
        }
        // Создаем объект бронирования без идентификатора
        Booking booking = Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .item(Item.builder().id(request.getItemId()).build())
                .booker(User.builder().id(userId).build())
                .status(BookingStatus.WAITING)
                .build();

        // Сохраняем запрос на бронирование в БД
        Booking bookingInDb = bookingRepository.save(booking);
        return new BookingDto(
                booking.getId(),
                bookingInDb.getStart(),
                bookingInDb.getEnd(),
                itemDto,
                userDto,
                bookingInDb.getStatus());
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approve) {
        // Получаем запрос на бронирование вещи
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Запрос на бронирование не найден");
        }
        Booking booking = bookingOpt.get();

        // Получаем идентификатор вещи, которую хотят забронировать
        long itemId = booking.getItem().getId();
        long ownerId = itemService.getItemOptionalById(itemId).getOwner().getId();

        // Проверяем, является ли пользователь владельцем вещи
        if (userId != ownerId) {
            throw new InternalServerException("Вы не являетесь владельцем вещи");
        }

        // Подтверждаем бронирование или отклоняем
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            ItemDto itemDtoUpdateAvailable = new ItemDto();
            itemDtoUpdateAvailable.setAvailable(false); // После подтверждения бронирования вещь становится недоступной к бронированию
            itemService.update(userId, itemId, itemDtoUpdateAvailable);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        // Обновляем статус бронирования в БД
        Booking bookingInDb = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingInDb);
    }

    @Override
    public BookingDto getBookingByBrokerOrOwner(long userId, long bookingId) {
        Booking booking = bookingRepository.getBookingByBrokerOrOwner(userId, bookingId);
        BookingDto bookingAnswerDto = bookingMapper.toBookingDto(booking);
        return bookingAnswerDto;
    }

    @Override
    public List<BookingDto> findAllBookingsByOwnerId(long userId) {
        List<Booking> bookings = bookingRepository.findAllBookingByBookerId(userId);
        log.info("Спискок бронирований получен");

        if (bookings.isEmpty()) {
            log.error("У пользователя c id {} нет бронирований", userId);
            throw new NotFoundException("У пользователя нет бронирований");
        }
        if (!bookings.stream().allMatch(booking -> userId == booking.getBooker().getId())) {
            log.error("Пользователь c id {} не автор бронирований", userId);
            throw new NotFoundException("Вы не являетесь автором бронирований");
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllBookingsByBookerId(long bookerId) {
        List<Booking> bookings = bookingRepository.findAllBookingByBookerId(bookerId);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .toList();
    }

}
