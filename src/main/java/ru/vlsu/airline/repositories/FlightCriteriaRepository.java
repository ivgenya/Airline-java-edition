package ru.vlsu.airline.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.controllers.TicketController;
import ru.vlsu.airline.dto.FlightPage;
import ru.vlsu.airline.dto.FlightSearchCriteria;

import ru.vlsu.airline.entities.Airline;
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
        Predicate predicate = getPredicate(flightSearchCriteria, flightRoot);
        criteriaQuery.where(predicate);
        setOrder(flightPage,criteriaQuery, flightRoot);
        logger.info("Query" + criteriaQuery);
        TypedQuery<Flight> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(flightPage.getPage() * flightPage.getPageSize());
        typedQuery.setMaxResults(flightPage.getPageSize());
        logger.info("Query" + typedQuery.toString());
        Pageable pageable = getPageble(flightPage);
        logger.info();

//        long flightsCount = getFligthsCount(predicate);
//        logger.info(String.valueOf(flightsCount));
        return new PageImpl<>(typedQuery.getResultList(), pageable, typedQuery.getResultList().size());
    }

//    private long getFligthsCount(Predicate predicate) {
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//        Root<Flight> countRoot = countQuery.from(Flight.class);
//        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
//        return entityManager.createQuery(countQuery).getSingleResult();
//
//    }

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
        logger.info(flightSearchCriteria.getArrivalAirport());
        logger.info(flightSearchCriteria.getDepartureAirport());
        logger.info(flightSearchCriteria.getDate());
        logger.info(String.valueOf(Objects.nonNull(flightSearchCriteria.getArrivalAirport())));
        if(Objects.nonNull(flightSearchCriteria.getArrivalAirport())){
            logger.info("First");
            predicates.add(
                    criteriaBuilder.like(flightRoot.join("schedule").join("arrivalAirport").get("name"), flightSearchCriteria.getArrivalAirport()
                    )
            );
        }
        if(Objects.nonNull(flightSearchCriteria.getDepartureAirport())){
            predicates.add(
                    criteriaBuilder.like(flightRoot.join("schedule").join("departureAirport").get("name"), flightSearchCriteria.getDepartureAirport())
            );
            logger.info("Scnd");
        }
        if(Objects.nonNull(flightSearchCriteria.getDate())){
            predicates.add(
                    criteriaBuilder.like(flightRoot.get("date"), flightSearchCriteria.getDate())
            );
            logger.info("Third");
        }
        logger.info("Past all ifs");
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
