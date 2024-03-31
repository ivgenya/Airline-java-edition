package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT f, MIN(seat.price) FROM Flight f " +
            "JOIN f.schedule s " +
            "JOIN s.departureAirport da " +
            "JOIN s.arrivalAirport aa " +
            "JOIN f.seats seat " +
            "WHERE da.city = :departureCity " +
            "AND aa.city = :arrivalCity " +
            "AND seat.status = 'available' " +
            "AND f.date = :date " +
            "GROUP BY f.id")
    List<Object[]> findByDepartureCityAndArrivalCityAndDate(
            @Param("departureCity") String departureCity,
            @Param("arrivalCity") String arrivalCity,
            @Param("date") LocalDate date);
    Optional<Flight> findByScheduleIdAndDate(int scheduleId, LocalDate date);

}
