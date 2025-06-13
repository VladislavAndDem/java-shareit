package ru.practicum.shareit.booking.controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                             @Valid @RequestBody NewBookingRequest request) {
        log.debug("Принят запрос на создание бронирования для пользователя с id={}, бронирование: {}", userId,
                request);
        return bookingService.create(userId, request);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto approved(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                               @PathVariable long bookingId,
                               @RequestParam boolean approved) {
        log.debug("Принят запрос на подтверждение статуса бронирования с id={} пользователем с id={}, статус: {}",
                bookingId, userId, approved);
        return bookingService.approve(userId, bookingId, approved);
    }

    ;

    @GetMapping(value = "/{bookingId}")
    public BookingDto findBookingOwnerOrBrokerById(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                                                   @PathVariable long bookingId) {
        log.debug("Принят запрос на получение бронирования владельцем вещи или с id={} пользователя с id={}", bookingId, userId);

        return bookingService.getBookingByBrokerOrOwner(userId, bookingId);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findAllBookingByBookerId(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Принят запрос на получение бронирований владельца с id={}", userId);
        return bookingService.findAllBookingsByOwnerId(userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookings(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Принят запрос на получение бронирований пользователя с id={}", userId);
        return bookingService.findAllBookingsByBookerId(userId);
    }
}
