package ru.vlsu.airline.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.dto.FlightSearchCriteria;
import ru.vlsu.airline.entities.Flight;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FlightCriteriaRepository{

    private static final Logger logger = LoggerFactory.getLogger(FlightCriteriaRepository.class);

    @Autowired
    private FlightRepository flightRepository;

    public Page<Flight> findAllFlightsWithFilters(FlightSearchCriteria flightSearchCriteria, Pageable pageable){
        Specification<Flight> specification = createSpecification(flightSearchCriteria);
        return flightRepository.findAll(specification, pageable);
    }

    private Specification<Flight> createSpecification(FlightSearchCriteria flightSearchCriteria) {
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            if (flightSearchCriteria.getDepartureAirport() != null && !flightSearchCriteria.getDepartureAirport().isEmpty()) {
                list.add(cb.like(root.get("schedule").get("departureAirport").get("name"), "%" + flightSearchCriteria.getDepartureAirport() + "%"));
            }

            if (flightSearchCriteria.getArrivalAirport() != null && !flightSearchCriteria.getArrivalAirport().isEmpty()) {
                list.add(cb.like(root.get("schedule").get("arrivalAirport").get("name"), "%" + flightSearchCriteria.getArrivalAirport() + "%"));
            }

            if (flightSearchCriteria.getScheduleNumber() != null && !flightSearchCriteria.getScheduleNumber().isEmpty()) {
                list.add(cb.like(root.get("schedule").get("number"), "%" + flightSearchCriteria.getScheduleNumber() + "%"));
            }

            Predicate[] predicates = list.toArray(new Predicate[0]);
            return cb.and(predicates);
        };
    }
}
