package ru.vlsu.airline.dto;

public class FlightSearchCriteria {

    private String planeName;

    private String date;

    public String getPlaneName() {
        return planeName;
    }

    public void setPlaneName(String planeName) {
        this.planeName = planeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
