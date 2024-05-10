package ru.vlsu.airline.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.vlsu.airline.dto.ScheduleModel;
import ru.vlsu.airline.entities.Schedule;


public interface IScheduleService {
    Page<Schedule> getAllSchedule(Pageable pageable);
    Schedule getScheduleById(int schduleId);
    int addSchedule(Schedule schedule);
    int updateSchedule(Schedule schedule);
    int deleteSchedule(int scheduleId);
    Schedule convertToEntity(ScheduleModel scheduleModel);
}
