package ru.vlsu.airline.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class FlightModel {

    @NotNull
    private int scheduleId;
    @NotNull
    private LocalDate date;
    @NotNull
    private int planeId;
    @NotNull
    private String type;
    @NotNull
    private String status;
    @NotNull
    private int gate;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getPlaneId() {
        return planeId;
    }

    public void setPlaneId(int planeId) {
        this.planeId = planeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGate() {
        return gate;
    }

    public void setGate(int gate) {
        this.gate = gate;
    }
}
