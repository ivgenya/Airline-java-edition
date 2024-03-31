package ru.vlsu.airline.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingModel {
    private int id;
    private String code;
    private LocalDateTime bookingDate;
    private String status;
    private List<BoardingPassModel> tickets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BoardingPassModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<BoardingPassModel> tickets) {
        this.tickets = tickets;
    }
}
