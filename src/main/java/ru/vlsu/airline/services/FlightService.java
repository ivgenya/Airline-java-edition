package ru.vlsu.airline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.FlightBoardModel;
import ru.vlsu.airline.dto.FlightModel;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Flight_seat;
import ru.vlsu.airline.entities.Plane;
import ru.vlsu.airline.entities.Schedule;
import ru.vlsu.airline.repositories.BookingRepository;
import ru.vlsu.airline.repositories.FlightRepository;
import ru.vlsu.airline.repositories.PlaneRepository;
import ru.vlsu.airline.repositories.ScheduleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService implements IFlightService{

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PlaneRepository planeRepository;
    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    public Flight getFlightById(int flightId) {
        Optional<Flight> optionalFlight = flightRepository.findById(flightId);
        if (optionalFlight.isPresent()) {
            return optionalFlight.get();
        }
        return null;
    }

    @Override
    public int addFlight(Flight flight) {
        Flight savedFlight = flightRepository.save(flight);
        return savedFlight.getId();
    }

    @Override
    public int updateFlight(Flight flight) {
        if (flightRepository.existsById(flight.getId())) {
            flightRepository.save(flight);
            return flight.getId();
        } else {
            return -1;
        }
    }

    @Override
    public int deleteFlight(int flightId) {
        if (flightRepository.existsById(flightId)) {
            flightRepository.deleteById(flightId);
            return flightId;
        } else {
            return -1;
        }
    }

    @Override
    public List<FlightBoardModel> getFlightsByCities(String departureCity, String arrivalCity, LocalDate date) {
        List<Object[]> flightObjects = flightRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
        List<FlightBoardModel> flightBoardModels = new ArrayList<>();

        for (Object[] row : flightObjects) {
            Flight flight = (Flight) row[0];
            Integer cheapestSeatPrice = (Integer) row[1];
            FlightBoardModel dto = convertToDto(flight);
            dto.setCheapestSeatPrice(cheapestSeatPrice.toString());
            flightBoardModels.add(dto);
        }

        return flightBoardModels;
    }


    private FlightBoardModel convertToDto(Flight flight) {
        FlightBoardModel dto = new FlightBoardModel();
        dto.setId(String.valueOf(flight.getId()));
        dto.setStatus(flight.getStatus());
        dto.setType(flight.getType());
        dto.setGate(String.valueOf(flight.getGate()));
        dto.setAirlineShortName(flight.getSchedule().getAirline().getShortName());
        dto.setScheduleNumber(String.valueOf(flight.getSchedule().getNumber()));
        dto.setDepartureAirportShortName(flight.getSchedule().getDepartureAirport().getShortName());
        dto.setDepartureAirportCity(flight.getSchedule().getDepartureAirport().getCity());
        dto.setArrivalAirportShortName(flight.getSchedule().getArrivalAirport().getShortName());
        dto.setArrivalAirportCity(flight.getSchedule().getArrivalAirport().getCity());
        dto.setDate(flight.getDate().toString());
        dto.setDepartureTime(flight.getSchedule().getDepartureTime().toString());
        dto.setArrivalTime(flight.getSchedule().getArrivalTime().toString());
        dto.setFlightDuration(flight.getSchedule().getFlightDuration().toString());
        return dto;
    }
    @Override
    public Flight convertToEntity(FlightModel flightModel) {
        Flight flight = new Flight();
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(flightModel.getScheduleId());
        if (optionalSchedule.isPresent()) {
            flight.setSchedule(optionalSchedule.get());
        }
        flight.setDate(flightModel.getDate());
        Optional<Plane> optionalPlane = planeRepository.findById(flightModel.getPlaneId());
        if (optionalPlane.isPresent()) {
            flight.setPlane(optionalPlane.get());
        }
        flight.setType(flightModel.getType());
        flight.setStatus(flightModel.getStatus());
        flight.setGate(flightModel.getGate());
        return flight;
    }
}
