package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.FlightBoardModel;
import ru.vlsu.airline.dto.FlightModel;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.services.IFlightService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private IFlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable int id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping
    public ResponseEntity<String> createSchedule(@Valid @RequestBody FlightModel flightModel) {
        Flight flight = flightService.convertToEntity(flightModel);
        int result = flightService.addFlight(flight);

        if (result > 0) {
            return new ResponseEntity<>("Рейс успешно создан", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Произошла ошибка при создании рейса", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateFlight(@PathVariable int id, @RequestBody Flight flight) {
        flight.setId(id);
        int updatedFlightId = flightService.updateFlight(flight);
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



}
