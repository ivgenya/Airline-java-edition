package ru.vlsu.airline.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.vlsu.airline.statemachine.model.TicketState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String code;

    @Column(name = "flight_id", nullable = false)
    private int flightId;

    @Column(name = "seat_id", nullable = false)
    private int seatId;

    @Column(name = "passenger_id", nullable = false)
    private int passengerId;

    @Column(name = "booking_id")
    @JsonIgnore
    private Integer bookingId;

    @Column(name = "user_id")
    @JsonIgnore
    private Integer userId;

    @Column(name = "date_of_purchase", nullable = false)
    private LocalDateTime dateOfPurchase;

    @Column(nullable = false)
    private String status;

    @Column(name = "baggage_type", nullable = false)
    private String baggageType;

    @ManyToOne
    @JoinColumn(name = "passenger_id", insertable = false, updatable = false)
    @JsonIgnore
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    @JsonIgnore
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    @JsonIgnore
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    @JsonIgnore
    private Flight_seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @JsonIgnore
    private TicketState state;

    public TicketState getState() {
        return state;
    }
    public void setState(TicketState state) {
        this.state = state;
    }



    public Ticket() {
        // TODO: добавить состояния
    }

    public Ticket(String code, String status, String baggageType) {
        this.code = code;
        this.status = status;
        this.baggageType = baggageType;
        this.dateOfPurchase = LocalDateTime.now();
        this.passenger = null;
        this.flight = null;
        this.booking = null;
        this.seat = null;
        this.user = null;
    }

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

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Flight_seat getSeat() {
        return seat;
    }

    public void setSeat(Flight_seat seat) {
        this.seat = seat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
