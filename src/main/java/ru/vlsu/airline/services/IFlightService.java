package ru.vlsu.airline.services;

import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.FlightBoardModel;
import ru.vlsu.airline.dto.FlightModel;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Schedule;

import java.time.LocalDate;
import java.util.List;


public interface IFlightService {
    List<Flight> getAllFlights();
    Flight getFlightById(int flightId);
    int addFlight(Flight flight);
    int updateFlight(Flight flight);
    int deleteFlight(int flightId);
    List<FlightBoardModel> getFlightsByCities(String departureCity, String arrivalCity, LocalDate date);
    Flight convertToEntity(FlightModel flightModel);
}
