package org.example.testprojectback.repository.intermediate;

import org.example.testprojectback.model.intermediate.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    void deleteByInterestId( Long interestId);
    void deleteByUserId( Long userId);
}
