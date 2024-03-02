package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Flight_seat;

@Repository
public interface FlightSeatRepository extends JpaRepository<Flight_seat, Integer> {
}
