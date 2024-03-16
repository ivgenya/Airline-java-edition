package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
}
