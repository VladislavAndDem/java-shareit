package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Override
    public BookingDto create(Long userId, NewBookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", request.getItemId());
                    return new NotFoundException("Вещь с id " + request.getItemId() + " не найдена");
                });

        if (!item.getAvailable()) {
            throw new InternalServerException("Вещь недоступна для бронирования");
        }

        validationDate(request.getStart(), request.getEnd());

        Booking booking = Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking bookingInDb = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingInDb);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approve) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Бронирование с id " + bookingId + " не найдено");
        }
        Booking booking = bookingOpt.get();

        long itemId = booking.getItem().getId();
        long ownerId = itemService.getItemOptionalById(itemId).getOwner().getId();

        if (userId != ownerId) {
            throw new InternalServerException("Вы не являетесь владельцем вещи");
        }

        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            ItemDto itemDtoUpdateAvailable = new ItemDto();
            itemDtoUpdateAvailable.setAvailable(false);
            itemService.update(userId, itemId, itemDtoUpdateAvailable);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking bookingInDb = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingInDb);
    }

    @Override
    public BookingDto getBookingByBrokerOrOwner(long userId, long bookingId) {
        return BookingMapper.toBookingDto(bookingRepository.getBookingByBrokerOrOwner(userId, bookingId));
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

    private void validationDate(LocalDateTime start, LocalDateTime end) {
        LocalDateTime today = LocalDateTime.now();
        if (start.isAfter(end) || start.isBefore(today)) {
            throw new InternalServerException("Неправильное время");
        }
    }

}
