package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vlsu.airline.entities.Airport;
import ru.vlsu.airline.entities.Terminal;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Integer> {
}
