package org.example.testprojectback.repository;

import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByNameStartingWith(String name);
    List<Group> findByInterestsIn(Set<Interest> interestSet);

    @Modifying
    @Query(value = "DELETE FROM group_interest WHERE interest_id = :interestId", nativeQuery = true)
    void removeInterestFromGroups(@Param("interestId") Long interestId);

    @Modifying
    @Query(value = "DELETE FROM groups_subscribers WHERE subscribers_id = :userId", nativeQuery = true)
    void removeUserFromGroups(@Param("userId") Long userId);

    @Override
    long count();
}
