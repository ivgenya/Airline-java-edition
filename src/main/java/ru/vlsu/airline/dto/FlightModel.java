package ru.vlsu.airline.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.vlsu.airline.entities.Flight;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FlightModel {
    private int id;
    @NotNull
    private int scheduleId;
    @NotNull
    private String scheduleNumber;
    @NotNull
    private String date;
    @NotNull
    private int planeId;
    @NotNull
    private String planeName;
    @NotNull
    private String arrivalAirport;
    @NotNull
    private String departureAirport;
    @NotNull
    private String type;
    @NotNull
    private String status;
    @NotNull
    private int gate;

    public String getPlaneName() {
        return planeName;
    }

    public void setPlaneName(String planeName) {
        this.planeName = planeName;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static FlightModel toFlightModel(Flight flight) {
        FlightModel flightModel = new FlightModel();
        flightModel.setId(flight.getId());
        flightModel.setScheduleNumber(flight.getSchedule().getNumber());
        flightModel.setArrivalAirport(flight.getSchedule().getArrivalAirport().getName());
        flightModel.setDepartureAirport(flight.getSchedule().getDepartureAirport().getName());
        flightModel.setScheduleId(flight.getSchedule().getId());
        String formattedDate = flight.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        flightModel.setDate(formattedDate);
        flightModel.setPlaneId(flight.getPlane().getId());
        flightModel.setPlaneName(flight.getPlane().getPlaneName());
        flightModel.setType(flight.getType());
        flightModel.setStatus(flight.getStatus());
        flightModel.setGate(flight.getGate());
        return flightModel;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
}
