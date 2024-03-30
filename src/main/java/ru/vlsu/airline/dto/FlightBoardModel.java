package ru.vlsu.airline.dto;

public class FlightBoardModel {
    private String id;
    private String type;
    private String status;
    private String gate;
    private String airlineShortName;
    private String scheduleNumber;
    private String departureAirportShortName;
    private String departureAirportCity;
    private String arrivalAirportShortName;
    private String arrivalAirportCity;
    private String date;
    private String departureTime;
    private String arrivalTime;
    private String flightDuration;
    private String cheapestSeatPrice;

    public String getAirlineShortName() {
        return airlineShortName;
    }

    public void setAirlineShortName(String airlineShortName) {
        this.airlineShortName = airlineShortName;
    }

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getDepartureAirportShortName() {
        return departureAirportShortName;
    }

    public void setDepartureAirportShortName(String departureAirportShortName) {
        this.departureAirportShortName = departureAirportShortName;
    }

    public String getDepartureAirportCity() {
        return departureAirportCity;
    }

    public void setDepartureAirportCity(String departureAirportCity) {
        this.departureAirportCity = departureAirportCity;
    }

    public String getArrivalAirportShortName() {
        return arrivalAirportShortName;
    }

    public void setArrivalAirportShortName(String arrivalAirportShortName) {
        this.arrivalAirportShortName = arrivalAirportShortName;
    }

    public String getArrivalAirportCity() {
        return arrivalAirportCity;
    }

    public void setArrivalAirportCity(String arrivalAirportCity) {
        this.arrivalAirportCity = arrivalAirportCity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(String flightDuration) {
        this.flightDuration = flightDuration;
    }

    public String getCheapestSeatPrice() {
        return cheapestSeatPrice;
    }

    public void setCheapestSeatPrice(String cheapestSeatPrice) {
        this.cheapestSeatPrice = cheapestSeatPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}
