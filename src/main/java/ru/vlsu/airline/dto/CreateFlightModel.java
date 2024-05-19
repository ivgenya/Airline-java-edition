package ru.vlsu.airline.dto;

import javax.validation.constraints.NotNull;

public class CreateFlightModel {
    @NotNull
    private Integer  scheduleId;
    @NotNull
    private String date;
    @NotNull
    private Integer  planeId;

    @NotNull
    private String type;
    @NotNull
    private String status;
    @NotNull
    private Integer  gate;

    public Integer  getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer  scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer  getPlaneId() {
        return planeId;
    }

    public void setPlaneId(Integer planeId) {
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

    public Integer  getGate() {
        return gate;
    }

    public void setGate(Integer  gate) {
        this.gate = gate;
    }
}
