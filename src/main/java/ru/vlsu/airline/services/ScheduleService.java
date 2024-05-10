package ru.vlsu.airline.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.dto.ScheduleModel;
import ru.vlsu.airline.entities.*;
import ru.vlsu.airline.repositories.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService implements IScheduleService{

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AirlineRepository airlineRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private TerminalRepository terminalRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Override
    public Page<Schedule> getAllSchedule(Pageable pageable) {
        return scheduleRepository.findAll(pageable);
    }

    @Override
    public Schedule getScheduleById(int scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            return optionalSchedule.get();
        }
        return null;
    }

    @Override
    public int addSchedule(Schedule schedule) {
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return savedSchedule.getId();
    }

    @Override
    public int updateSchedule(Schedule schedule) {
        if (scheduleRepository.existsById(schedule.getId())) {
            scheduleRepository.save(schedule);
            return schedule.getId();
        } else {
            return -1;
        }
    }

    @Override
    public int deleteSchedule(int scheduleId) {
        if (scheduleRepository.existsById(scheduleId)) {
            scheduleRepository.deleteById(scheduleId);
            return scheduleId;
        } else {
            return -1;
        }
    }

    @Override
    public Schedule convertToEntity(ScheduleModel scheduleModel) {
        Schedule schedule = new Schedule();
        schedule.setId(scheduleModel.getId());
        Optional<Airline> optionalAirline = airlineRepository.findById(scheduleModel.getAirlineId());
        if (optionalAirline.isPresent()) {
            schedule.setAirline(optionalAirline.get());
        }
        schedule.setNumber(scheduleModel.getNumber());
        Optional<Airport> optionalAirport = airportRepository.findById(scheduleModel.getDepartureAirportId());
        if (optionalAirport.isPresent()) {
            schedule.setDepartureAirport(optionalAirport.get());
        }
        Optional<Airport> optionalArrivalAirport = airportRepository.findById(scheduleModel.getArrivalAirportId());
        if (optionalArrivalAirport.isPresent()) {
            schedule.setArrivalAirport(optionalArrivalAirport.get());
        }
        String departureTimeString = scheduleModel.getDepartureTime();
        logger.info(departureTimeString);
        LocalTime departureTime = LocalTime.parse(departureTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        schedule.setDepartureTime(departureTime);
        String arrivalTimeString = scheduleModel.getArrivalTime();
        logger.info(arrivalTimeString);
        LocalTime arrivalTime = LocalTime.parse(arrivalTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        schedule.setArrivalTime(arrivalTime);
        String flightDurationString = scheduleModel.getFlightDuration();
        logger.info(flightDurationString);
        LocalTime flightDuration = LocalTime.parse(flightDurationString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        schedule.setFlightDuration(flightDuration);
        Optional<Terminal> optionalTerminal = terminalRepository.findById(scheduleModel.getTerminal());
        if (optionalTerminal.isPresent()) {
            schedule.setTerminal(optionalTerminal.get());
        }
        return schedule;
    }
}
