package ru.vlsu.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlsu.airline.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
