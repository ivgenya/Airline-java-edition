package ru.vlsu.airline.controllers;

import org.hibernate.collection.internal.PersistentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.FlightBoardModel;
import ru.vlsu.airline.dto.FlightModel;
import ru.vlsu.airline.dto.SeatModel;
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

    @GetMapping
    public ResponseEntity<List<FlightModel>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        List<FlightModel> flightModels = new ArrayList<FlightModel>();
        for(Flight fl: flights){
            flightModels.add(toFlightModel(fl));
        }
        return ResponseEntity.ok(flightModels);
    }

    public static FlightModel toFlightModel(Flight flight) {
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
    public ResponseEntity<List<FlightBoardModel>> getFlightsBoard(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FlightBoardModel> flights = flightService.getFlightsByCities(departureCity, arrivalCity, date);
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
