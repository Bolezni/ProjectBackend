package org.example.testprojectback.repository;

import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findByInterestsNameIn(Set<String> interestNames);

    Optional<User> findUserByActivateCode(String activateCode);
}
