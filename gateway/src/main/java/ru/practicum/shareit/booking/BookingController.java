package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingDto;

@RestController
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookingController {
    BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody NewBookingDto newBookingDto) {
        return bookingClient.createBooking(userId, newBookingDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", defaultValue = "all")
                                                          String stateParam,
                                                          @PositiveOrZero
                                                          @RequestParam(name = "from", defaultValue = "0")
                                                          Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10")
                                                          Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неверный state: " + stateParam));
        return bookingClient.getAllBookingsForBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getAllBookingsForOwner(userId, state);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("bookingId") Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable("bookingId") Long bookingId,
                                                      @RequestParam(name = "approved", required = true) Boolean approved) {
        return bookingClient.updateStatusBooking(userId, bookingId, approved);
    }
}
