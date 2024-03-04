package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Flight_seat;

import java.util.List;

@Repository
public interface FlightSeatRepository extends JpaRepository<Flight_seat, Integer> {
    List<Flight_seat> findByFlightId(int flightId);
}
