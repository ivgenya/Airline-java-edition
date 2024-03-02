package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.TicketStateEntity;
import ru.vlsu.airline.statemachine.model.TicketState;

@Repository
public interface TicketStateRepository extends JpaRepository<TicketStateEntity, Integer> {
}

