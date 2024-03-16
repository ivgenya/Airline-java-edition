package ru.vlsu.airline.services;

import ru.vlsu.airline.dto.ScheduleModel;
import ru.vlsu.airline.entities.Schedule;

import java.util.List;

public interface IScheduleService {
    List<Schedule> getAllSchedule();
    Schedule getScheduleById(int schduleId);
    int addSchedule(Schedule schedule);
    int updateSchedule(Schedule schedule);
    int deleteSchedule(int scheduleId);
    Schedule convertToEntity(ScheduleModel scheduleModel);
}
