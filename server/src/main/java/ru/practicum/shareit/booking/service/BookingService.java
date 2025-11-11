package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, NewBookingRequest bookingDto);

    BookingDto approve(Long userId, Long bookingId, boolean approve);

    BookingDto getBookingByBrokerOrOwner(long userId, long bookerId);

    List<BookingDto> findAllBookingsByOwnerId(long userId);

    List<BookingDto> findAllBookingsByBookerId(long userId);
}
