package ru.vlsu.airline.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface IFlightService {
    Flight getFlightById(int flightId);
    int addFlight(Flight flight);
    int updateFlight(Flight flight);
    int deleteFlight(int flightId);
    Page<FlightBoardModel> getFlightsByCities(String departureCity, String arrivalCity, LocalDate date, Pageable pageable);
    Flight convertToEntity(FlightModel flightModel);
    List<SeatModel> getSeatsByFlightId(int flightId);
    Optional<Flight> findFlightByAirlineShortNameNumberAndDate(String name, LocalDate date);
    FlightBoardModel convertToDto(Flight flight);
    Page<Flight> getFlights(FlightPage flightPage, FlightSearchCriteria flightSearchCriteria);
}
