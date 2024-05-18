package ru.vlsu.airline.dto;

public class FlightFilterRequest {
    private FlightPage flightPage;
    private FlightSearchCriteria flightSearchCriteria;

    public FlightSearchCriteria getFlightSearchCriteria() {
        return flightSearchCriteria;
    }

    public void setFlightSearchCriteria(FlightSearchCriteria flightSearchCriteria) {
        this.flightSearchCriteria = flightSearchCriteria;
    }

    public FlightPage getFlightPage() {
        return flightPage;
    }

    public void setFlightPage(FlightPage flightPage) {
        this.flightPage = flightPage;
    }
}
