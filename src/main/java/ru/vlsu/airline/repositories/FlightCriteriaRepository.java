package ru.vlsu.airline.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.dto.FlightPage;
import ru.vlsu.airline.dto.FlightSearchCriteria;
import ru.vlsu.airline.entities.Airport;
import ru.vlsu.airline.entities.Flight;
import ru.vlsu.airline.entities.Schedule;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
public class FlightCriteriaRepository {

    private static final Logger logger = LoggerFactory.getLogger(FlightCriteriaRepository.class);

    private EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;

    public FlightCriteriaRepository(EntityManager entityManager){
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Flight> findAllFlightsWithFilters(FlightPage flightPage,
                                                  FlightSearchCriteria flightSearchCriteria){
        CriteriaQuery<Flight> criteriaQuery = criteriaBuilder.createQuery(Flight.class);
        Root<Flight> flightRoot = criteriaQuery.from(Flight.class);
        if(flightSearchCriteria.getArrivalAirport() != null && flightSearchCriteria.getDepartureAirport() != null && flightSearchCriteria.getScheduleNumber() != null){
            Predicate predicate = getPredicate(flightSearchCriteria, flightRoot);
            criteriaQuery.where(predicate);
        }
        setOrder(flightPage,criteriaQuery, flightRoot);
        TypedQuery<Flight> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(flightPage.getPage() * flightPage.getPageSize());
        typedQuery.setMaxResults(flightPage.getPageSize());
        Pageable pageable = getPageble(flightPage);

        return new PageImpl<>(typedQuery.getResultList(), pageable, typedQuery.getResultList().size());
    }

    private Pageable getPageble(FlightPage flightPage) {
        Sort sort = Sort.by(flightPage.getSortDirection(), flightPage.getSortBy());
        return PageRequest.of(flightPage.getPage(), flightPage.getPageSize(), sort);
    }

    private void setOrder(FlightPage flightPage, CriteriaQuery<Flight> criteriaQuery, Root<Flight> flightRoot) {
        if(flightPage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(flightRoot.get(flightPage.getSortBy())));
        }else{
            criteriaQuery.orderBy(criteriaBuilder.desc(flightRoot.get(flightPage.getSortBy())));
        }
    }

    private Predicate getPredicate(FlightSearchCriteria flightSearchCriteria, Root<Flight> flightRoot) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Flight, Schedule> scheduleJoin = flightRoot.join("schedule");
        Join<Flight, Airport> airportAJoin = scheduleJoin.join("arrivalAirport");
        Join<Flight, Airport> airportDJoin = scheduleJoin.join("departureAirport");
        if(Objects.nonNull(flightSearchCriteria.getArrivalAirport())){
            predicates.add(
                    criteriaBuilder.equal(airportAJoin.get("name"), flightSearchCriteria.getArrivalAirport())
            );
        }
        if(Objects.nonNull(flightSearchCriteria.getDepartureAirport())){
            predicates.add(
                    criteriaBuilder.equal(airportDJoin.get("name"), flightSearchCriteria.getDepartureAirport())
            );
        }
        if(Objects.nonNull(flightSearchCriteria.getScheduleNumber())){
            predicates.add(
                    criteriaBuilder.equal(scheduleJoin.get("number"), flightSearchCriteria.getScheduleNumber())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
