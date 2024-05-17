package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.*;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.services.IFlightService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private IFlightService flightService;
//    @GetMapping
//    public ResponseEntity<Page<FlightModel>> getAllFlights(@RequestParam(defaultValue = "0") int page,
//                                                           @RequestParam(defaultValue = "10") int size,
//                                                           @RequestParam(required = false) String sortBy,
//                                                           @RequestParam(required = false) String sortDirection) {
//        Pageable pageable;
//        if(sortBy != null && sortDirection != null){
//            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
//            Sort sort = Sort.by(direction, sortBy);
//            pageable = PageRequest.of(page, size, sort);
//        }else{
//            pageable = PageRequest.
//        }
//        Page<Flight> flightsPage = flightService.getAllFlights(PageRequest.of(page, size));
//        Page<FlightModel> flightModels = flightsPage.map(this::toFlightModel);
//        return ResponseEntity.ok(flightModels);
//    }

    @GetMapping("test-page")
    public ResponseEntity<Page<Flight>> testPage(){
        FlightPage flightPage = new FlightPage();
        FlightSearchCriteria flightSearchCriteria = new FlightSearchCriteria();
        flightSearchCriteria.setArrivalAirport("Zvartnots");
        flightSearchCriteria.setDepartureAirport("Sheremetyevo");
        return new ResponseEntity<>(flightService.getFlights(flightPage, flightSearchCriteria), HttpStatus.OK);
    }

    public FlightModel toFlightModel(Flight flight) {
        FlightModel flightModel = new FlightModel();
        flightModel.setId(flight.getId());
        flightModel.setScheduleId(flight.getSchedule().getId());
        String formattedDate = flight.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        flightModel.setDate(formattedDate);
        flightModel.setPlaneId(flight.getPlane().getId());
        flightModel.setType(flight.getType());
        flightModel.setStatus(flight.getStatus());
        flightModel.setGate(flight.getGate());
        return flightModel;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightModel> getFlightById(@PathVariable int id) {
        FlightModel flight = toFlightModel(flightService.getFlightById(id));
        return ResponseEntity.ok(flight);
    }

    @PostMapping
    public ResponseEntity<String> createFlight(@Valid @RequestBody FlightModel flightModel) {
        Flight flight = flightService.convertToEntity(flightModel);
        int result = flightService.addFlight(flight);

        if (result > 0) {
            return new ResponseEntity<>("Рейс успешно создан", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Произошла ошибка при создании рейса", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateFlight(@PathVariable int id, @RequestBody FlightModel flight) {
        int updatedFlightId = flightService.updateFlight(flightService.convertToEntity(flight));
        if (updatedFlightId != -1) {
            return ResponseEntity.ok(updatedFlightId);
        } else {
            return new ResponseEntity<>("Рейс не найден", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deleteFlight(@PathVariable int id) {
        int deletedFlightId = flightService.deleteFlight(id);
        if (deletedFlightId != -1) {
            return ResponseEntity.ok(deletedFlightId);
        } else {
            return new ResponseEntity<>("Рейс не найден", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/board")
    public ResponseEntity<Page<FlightBoardModel>> getFlightsBoard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Page<FlightBoardModel> flights = flightService.getFlightsByCities(departureCity, arrivalCity, date, PageRequest.of(page, size));
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/seats/{flightId}")
    public ResponseEntity<List<SeatModel>> getSeats(@PathVariable int flightId) {
        List<SeatModel> seats = flightService.getSeatsByFlightId(flightId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<?> getByAirlineNameAndDate(@RequestParam("airlineName") String airlineName, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<Flight> flight = flightService.findFlightByAirlineShortNameNumberAndDate(airlineName, date);
        if(flight.isPresent()){
            return ResponseEntity.ok(flightService.convertToDto(flight.get()));
        }
        return new ResponseEntity<>("Рейс не найден", HttpStatus.NOT_FOUND);
    }

}
