package org.example.testprojectback.repository;

import org.example.testprojectback.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByName(String name);

    Set<Interest> findAllByNameIn(Set<String> names);

    Set<Interest> findByNameIn(Set<String> interestNames);

    long count();
}
