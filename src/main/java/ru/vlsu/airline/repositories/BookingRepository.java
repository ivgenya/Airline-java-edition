package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Booking;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByStatus(String status);
    Optional<Booking> findByCode(String code);
}
