package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    UserDTO user1;
    UserDTO user2;
    ItemDto item1;

    @BeforeAll
    void beforeAll() {
        user1 = UserDTO.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = UserDTO.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = ItemDto.builder().name("Yandex").description("YandexPracticum").available(true).build();
    }

    @Test
    void create() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);

        assertThat(newBooking.getId()).isNotNull();
        assertThat(newBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(newBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(newBooking.getItem().getId()).isEqualTo(booking.getItemId());
        assertThat(newBooking.getBooker().getId()).isEqualTo(user4.getId());
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    /*@Test
    void approve() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        BookingDto approvedBooking = bookingService.approve(user3.getId(), newBooking.getId(), true);

        assertThat(approvedBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(approvedBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(approvedBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(approvedBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(approvedBooking.getBooker()).isEqualTo(user4);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }*/

    @Test
    void getBookingByBrokerOrOwner() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto bookingDto = bookingService.create(user4.getId(), booking);
        BookingDto getBooking = bookingService.getBookingByBrokerOrOwner(user4.getId(), bookingDto.getId());
        BookingDto getBookingOwner = bookingService.getBookingByBrokerOrOwner(user3.getId(), bookingDto.getId());

        assertThat(getBooking.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBooking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBooking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBooking.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBooking.getBooker()).isEqualTo(user4);
        assertThat(getBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(getBookingOwner.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBookingOwner.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBookingOwner.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBookingOwner.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBookingOwner.getBooker()).isEqualTo(user4);
        assertThat(getBookingOwner.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    /*@Test
    void findAllBookingsByOwnerId() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.findAllBookingsByOwnerId(user3.getId()).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }*/

    @Test
    void findAllBookingsByBookerId() {
        UserDTO user3 = userService.createUser(user1);
        UserDTO user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.create(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.findAllBookingsByBookerId(user4.getId()).stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}