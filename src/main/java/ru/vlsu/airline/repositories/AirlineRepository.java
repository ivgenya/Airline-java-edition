package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Airline;
import ru.vlsu.airline.entities.Airport;
import ru.vlsu.airline.entities.Booking;

import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {
    Optional<Airline> findByShortName(String code);
}
