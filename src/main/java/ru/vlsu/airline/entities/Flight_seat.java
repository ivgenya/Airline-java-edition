package ru.vlsu.airline.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Flight_seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "plane_seat_id", nullable = false)
    @JsonIgnore
    private Plane_seat planeSeat;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    @JsonIgnore
    private Flight flight;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String status;

    public Flight_seat() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Plane_seat getPlaneSeat() {
        return planeSeat;
    }

    public void setPlaneSeat(Plane_seat planeSeat) {
        this.planeSeat = planeSeat;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
