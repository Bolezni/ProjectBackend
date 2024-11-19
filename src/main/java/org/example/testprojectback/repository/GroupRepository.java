package org.example.testprojectback.repository;

import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.Interest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
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

    @Query("SELECT g FROM Group g WHERE g.creator.username = :username")
    Page<Group> findByCreatorUsername(@Param("username") String username, Pageable pageable);

    @Override
    long count();

    @Query("SELECT g FROM User u JOIN u.subscribedGroups g WHERE u.username = :userName")
    Page<Group> findSubscribedGroupsByUserName(@Param("userName") String userName, Pageable pageable);


    @Query(value = """
        SELECT i.name AS interestName, 
               g.id AS groupId, 
               g.name AS groupName, 
               g.color AS groupColor, 
               g.description AS groupDescription,
               COUNT(s.id) AS subscriberCount
        FROM group_interest gi
        JOIN interests i ON gi.interest_id = i.id
        JOIN groups g ON gi.group_id = g.id
        LEFT JOIN groups_subscribers gs ON g.id = gs.group_id
        LEFT JOIN users s ON gs.subscribers_id = s.id
        GROUP BY i.name, g.id
        ORDER BY i.name, subscriberCount DESC
    """, nativeQuery = true)
    List<Map<String, Object>> findTopGroupsWithSubscribersByInterest();
}
