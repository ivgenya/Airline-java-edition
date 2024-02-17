package ru.vlsu.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlsu.airline.dto.ScheduleModel;
import ru.vlsu.airline.entities.Schedule;
import ru.vlsu.airline.services.IScheduleService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedule());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable int id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @PostMapping
    public ResponseEntity<String> createSchedule(@Valid @RequestBody ScheduleModel scheduleModel) {
        Schedule schedule = convertToEntity(scheduleModel);
        int result = scheduleService.addSchedule(schedule);

        if (result > 0) {
            return new ResponseEntity<>("Schedule успешно создано", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Произошла ошибка при создании schedule", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateSchedule(@PathVariable int id, @RequestBody Schedule schedule) {
        schedule.setId(id);
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

    private Schedule convertToEntity(ScheduleModel scheduleModel) {
        Schedule schedule = new Schedule();
        schedule.setAirlineId(scheduleModel.getAirlineId());
        schedule.setNumber(scheduleModel.getNumber());
        schedule.setDepartureAirportId(scheduleModel.getDepartureAirportId());
        schedule.setArrivalAirportId(scheduleModel.getArrivalAirportId());
        schedule.setDepartureTime(scheduleModel.getDepartureTime());
        schedule.setArrivalTime(scheduleModel.getArrivalTime());
        schedule.setFlightDuration(scheduleModel.getFlightDuration());
        schedule.setTerminal(scheduleModel.getTerminal());
        return schedule;
    }

}
