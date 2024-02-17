package ru.vlsu.airline.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "plane")
public class Plane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "plane_name", nullable = false)
    private String planeName;

    @OneToMany(mappedBy = "plane")
    @JsonIgnore
    private List<Flight> flights;

    @OneToMany(mappedBy = "plane")
    @JsonIgnore
    private List<Plane_seat> seats;

    public Plane() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaneName() {
        return planeName;
    }

    public void setPlaneName(String planeName) {
        this.planeName = planeName;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public List<Plane_seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Plane_seat> seats) {
        this.seats = seats;
    }
}
