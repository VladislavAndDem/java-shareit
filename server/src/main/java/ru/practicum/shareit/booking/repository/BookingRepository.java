package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("FROM Booking b " +
            "WHERE b.id = :bookingId AND (b.booker.id = :userId " +
            "OR EXISTS (SELECT i FROM Item i WHERE i.owner.id = :userId AND i = b.item))")
    Booking getBookingByBrokerOrOwner(@Param("userId") long userId, @Param("bookingId") long bookingId);

    @Query("FROM Booking b WHERE b.booker.id = :userId")
    List<Booking> findAllBookingByBookerId(@Param("userId") long userId);

    @Query("FROM Booking b WHERE b.booker.id = :userId AND b.item.id = :itemId")
    List<Booking> findAllBookingByBookerIdAndItemId(@Param("userId") long userId, @Param("itemId") long itemId);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sortOrder);
}
