package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
}
