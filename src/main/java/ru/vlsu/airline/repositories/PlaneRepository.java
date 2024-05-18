package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Plane;
import ru.vlsu.airline.entities.Schedule;

import java.util.Optional;

@Repository
public interface PlaneRepository extends JpaRepository<Plane, Integer> {
    Optional<Plane> findByPlaneName(String name);
}
