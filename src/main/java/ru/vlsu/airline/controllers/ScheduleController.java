package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.ScheduleModel;
import ru.vlsu.airline.entities.Schedule;
import ru.vlsu.airline.services.IScheduleService;

import javax.validation.Valid;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<Page<ScheduleModel>> getAllSchedules(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<Schedule> schedulePage = scheduleService.getAllSchedule(PageRequest.of(page, size));
        Page<ScheduleModel> scheduleModels = schedulePage.map(this::toScheduleModel);
        return ResponseEntity.ok(scheduleModels);
    }

    public ScheduleModel toScheduleModel(Schedule schedule) {
        ScheduleModel scheduleModel = new ScheduleModel();
        scheduleModel.setId(schedule.getId());
        scheduleModel.setAirlineId(schedule.getAirline().getId());
        scheduleModel.setNumber(schedule.getNumber());
        scheduleModel.setDepartureAirportId(schedule.getDepartureAirport().getId());
        scheduleModel.setArrivalAirportId(schedule.getArrivalAirport().getId());
        LocalTime departureTime = schedule.getDepartureTime();
        String formattedDepartureTime = departureTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        scheduleModel.setDepartureTime(formattedDepartureTime);
        LocalTime arrivalTime = schedule.getArrivalTime();
        String formattedArrivalTime = arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        scheduleModel.setArrivalTime(formattedArrivalTime);
        LocalTime flightDuration = schedule.getFlightDuration();
        String formattedFlightDuration = flightDuration.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        scheduleModel.setFlightDuration(formattedFlightDuration);
        scheduleModel.setTerminal(schedule.getTerminal().getId());
        return scheduleModel;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleModel> getScheduleById(@PathVariable int id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(toScheduleModel(schedule));
    }

    @PostMapping
    public ResponseEntity<String> createSchedule(@Valid @RequestBody ScheduleModel scheduleModel) {
        Schedule schedule = scheduleService.convertToEntity(scheduleModel);
        int result = scheduleService.addSchedule(schedule);

        if (result > 0) {
            return new ResponseEntity<>("Schedule успешно создано", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Произошла ошибка при создании schedule", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateSchedule(@PathVariable int id, @RequestBody ScheduleModel scheduleModel) {
        Schedule schedule = scheduleService.convertToEntity(scheduleModel);
        int updatedScheduleId = scheduleService.updateSchedule(schedule);
        if (updatedScheduleId != -1) {
            return ResponseEntity.ok(updatedScheduleId);
        } else {
            return new ResponseEntity<>("Schedule не найден", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deleteSchedule(@PathVariable int id) {
        int deletedScheduleId = scheduleService.deleteSchedule(id);
        if (deletedScheduleId != -1) {
            return ResponseEntity.ok(deletedScheduleId);
        } else {
            return new ResponseEntity<>("Schedule не найден", HttpStatus.NOT_FOUND);
        }
    }



}
