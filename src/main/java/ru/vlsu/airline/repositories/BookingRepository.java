package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByStatus(String status);
    Optional<Booking> findByCode(String code);
    @Query("SELECT b FROM Booking b JOIN FETCH b.tickets WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithTickets(@Param("bookingId") Integer bookingId);
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.tickets t WHERE t.user = :user")
    List<Booking> findByUser(@Param("user") User user);

}
