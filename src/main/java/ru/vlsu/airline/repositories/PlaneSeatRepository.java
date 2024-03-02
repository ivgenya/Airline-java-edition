package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Plane_seat;

@Repository
public interface PlaneSeatRepository extends JpaRepository<Plane_seat, Integer> {
}
