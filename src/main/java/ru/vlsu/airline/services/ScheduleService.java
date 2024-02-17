package ru.vlsu.airline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Schedule;
import ru.vlsu.airline.repositories.ScheduleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService implements IScheduleService{

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAllSchedule() {
        return scheduleRepository.findAll();
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
}
