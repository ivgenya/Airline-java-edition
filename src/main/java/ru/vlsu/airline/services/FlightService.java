package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlsu.airline.controllers.TicketController;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.*;
import ru.vlsu.airline.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService implements IFlightService{
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightCriteriaRepository flightCriteriaRepository;
    @Autowired
    private AirlineRepository airlineRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PlaneRepository planeRepository;
    @Autowired
    private FlightSeatRepository seatRepository;
    @Override
    public Page<Flight> getAllFlights(Pageable pageable) {
//        Flight flights = flightRepository.findAll(pageable)
        return flightRepository.findAll(pageable);
    }

//    private FlightModel convertFlightToDTO(Flight flight){
//
//    }

    public Page<Flight> getFlights(FlightPage flightPage,
                                   FlightSearchCriteria flightSearchCriteria){
        return flightCriteriaRepository.findAllFlightsWithFilters(flightPage, flightSearchCriteria);
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
    @Transactional
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
    public Page<FlightBoardModel> getFlightsByCities(String departureCity, String arrivalCity, LocalDate date, Pageable pageable) {
        Page<Object[]> flightObjects = flightRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date, pageable);
        List<Object[]> totalFlightObjects = flightRepository.findByDepartureCityAndArrivalCityAndDate(departureCity, arrivalCity, date);
        int totalElements = totalFlightObjects.size();
        logger.info(String.valueOf(totalElements));
        logger.info(String.valueOf(pageable.getPageSize()));
        logger.info(String.valueOf((double)totalElements));
        logger.info(String.valueOf((double)pageable.getPageSize()));
        int totalPages = (int) Math.ceil((double) totalElements / (double) pageable.getPageSize());
        logger.info(String.valueOf(totalPages));
        List<FlightBoardModel> flightBoardModels = new ArrayList<>();
        for (Object[] row : flightObjects) {
            Flight flight = (Flight) row[0];
            LocalDate departureDate = flight.getDate();
            if (departureDate.isAfter(LocalDate.now())) {
                Integer cheapestSeatPrice = (Integer) row[1];
                FlightBoardModel dto = convertToDto(flight);
                dto.setCheapestSeatPrice(cheapestSeatPrice.toString());
                flightBoardModels.add(dto);
            }
        }
        return new PageImpl<>(flightBoardModels, pageable, totalElements);
    }


    @Override
    public FlightBoardModel convertToDto(Flight flight) {
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
        flight.setId(flightModel.getId());
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(flightModel.getScheduleId());
        if (optionalSchedule.isPresent()) {
            flight.setSchedule(optionalSchedule.get());
        }
        LocalDate date = LocalDate.parse(flightModel.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        flight.setDate(date);
        Optional<Plane> optionalPlane = planeRepository.findById(flightModel.getPlaneId());
        if (optionalPlane.isPresent()) {
            flight.setPlane(optionalPlane.get());
        }
        flight.setType(flightModel.getType());
        flight.setStatus(flightModel.getStatus());
        flight.setGate(flightModel.getGate());
        return flight;
    }

    @Override
    public List<SeatModel> getSeatsByFlightId(int flightId){
        List<Flight_seat> seats = seatRepository.findByFlightId(flightId);
        List<SeatModel> seatModels = new ArrayList<SeatModel>();
        for(Flight_seat fs: seats){
            if(fs.getStatus().equals("available")){
                SeatModel seat = new SeatModel();
                seat.setId(fs.getId());
                seat.setSeatClass(fs.getPlaneSeat().getSeatClass());
                seat.setFlightId(fs.getFlight().getId());
                seat.setNumber(fs.getPlaneSeat().getNumber());
                seat.setPrice(fs.getPrice());
                seat.setStatus(fs.getStatus());
                seatModels.add(seat);
            }
        }
        return seatModels;
    }
    @Override
    public Optional<Flight> findFlightByAirlineShortNameNumberAndDate(String name,  LocalDate date) {
        String airlineCode = name.substring(0, 2);
        String flightNumber = name.substring(2);
        Optional<Airline> airline = airlineRepository.findByShortName(airlineCode);
        if(airline.isPresent()){
            Optional<Schedule> scheduleOptional = scheduleRepository.findByAirlineIdAndNumber(airline.get().getId(), Integer.parseInt(flightNumber));
            if(scheduleOptional.isPresent()){
                return flightRepository.findByScheduleIdAndDate(scheduleOptional.get().getId(), date);
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

}
