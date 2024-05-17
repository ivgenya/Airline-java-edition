package ru.vlsu.airline.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import ru.vlsu.airline.dto.FlightPage;
import ru.vlsu.airline.dto.FlightSearchCriteria;

import ru.vlsu.airline.entities.Flight;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FlightCriteriaRepository {

    @Autowired
    private CriteriaBuilder criteriaBuilder;

    @Autowired
    private EntityManager entityManager;


    public Page<Flight> findAllFlightsWithFilters(FlightPage flightPage,
                                                  FlightSearchCriteria flightSearchCriteria){
        CriteriaQuery<Flight> criteriaQuery = criteriaBuilder.createQuery(Flight.class);
        Root<Flight> flightRoot = criteriaQuery.from(Flight.class);
        Predicate predicate = getPredicate(flightSearchCriteria, flightRoot);
        criteriaQuery.where(predicate);
        setOrder(flightPage,criteriaQuery, flightRoot);

        TypedQuery<Flight> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(flightPage.getPage() * flightPage.getPageSize());
        typedQuery.setMaxResults(flightPage.getPageSize());

        Pageable pageable = getPageble(flightPage);

        long flightsCount = getFligthsCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, flightsCount);
    }

    private long getFligthsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Flight> countRoot = countQuery.from(Flight.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();

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
        if(Objects.nonNull(flightSearchCriteria.getPlaneName())){
            criteriaBuilder.like(flightRoot.get("planeName"), flightSearchCriteria.getPlaneName()
            );
        }
        if(Objects.nonNull(flightSearchCriteria.getDate())){
            criteriaBuilder.like(flightRoot.get("date"), flightSearchCriteria.getDate()
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
