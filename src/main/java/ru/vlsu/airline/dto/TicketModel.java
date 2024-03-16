package ru.vlsu.airline.dto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TicketModel {
    private String code;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfPurchase;
    private String status;
    private String baggageType;
    private int passengerId;
    private int flightId;
    private Integer bookingId;
    private int seatId;
    private Integer userId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(LocalDateTime dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBaggageType() {
        return baggageType;
    }

    public void setBaggageType(String baggageType) {
        this.baggageType = baggageType;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
