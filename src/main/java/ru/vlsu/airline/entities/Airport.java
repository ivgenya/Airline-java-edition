package ru.vlsu.airline.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "airport")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "arrivalAirport")
    private List<Schedule> scheduleArrivalAirports = new ArrayList<Schedule>();

    @OneToMany(mappedBy = "departureAirport")
    private List<Schedule> scheduleDepartureAirports = new ArrayList<Schedule>();

    @OneToMany(mappedBy = "airport")
    private List<Terminal> terminalAirports = new ArrayList<Terminal>();


    public Airport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Schedule> getScheduleArrivalAirports() {
        return scheduleArrivalAirports;
    }

    public void setScheduleArrivalAirports(List<Schedule> scheduleArrivalAirports) {
        this.scheduleArrivalAirports = scheduleArrivalAirports;
    }

    public List<Schedule> getScheduleDepartureAirports() {
        return scheduleDepartureAirports;
    }

    public void setScheduleDepartureAirports(List<Schedule> scheduleDepartureAirports) {
        this.scheduleDepartureAirports = scheduleDepartureAirports;
    }

    public List<Terminal> getTerminalAirports() {
        return terminalAirports;
    }

    public void setTerminalAirports(List<Terminal> terminalAirports) {
        this.terminalAirports = terminalAirports;
    }
}

