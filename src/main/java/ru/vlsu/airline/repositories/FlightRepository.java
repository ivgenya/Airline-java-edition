package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Flight;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT f FROM Flight f " +
            "JOIN f.schedule s " +
            "JOIN s.departureAirport da " +
            "JOIN s.arrivalAirport aa " +
            "WHERE da.city = :departureCity " +
            "AND aa.city = :arrivalCity " +
            "AND f.date = :date")
    List<Flight> findByDepartureCityAndArrivalCityAndDate(
            @Param("departureCity") String departureCity,
            @Param("arrivalCity") String arrivalCity,
            @Param("date") LocalDate date);

}
