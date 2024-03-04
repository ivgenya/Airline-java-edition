package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByStatus(String status);
    List<Ticket> findByBookingId(int bookingId);
    Optional<Ticket> findByCode(String code);

}
