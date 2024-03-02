package ru.vlsu.airline.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TicketStateEntity {
    @Id
    private Integer id;
    private String state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
