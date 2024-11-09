package org.example.testprojectback.repository;

import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findByInterestsNameIn(Set<String> interestNames);
    Optional<User> findUserByActivateCode(String activateCode);
    long count();

    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND u.id != :groupCreatorId AND u NOT IN (SELECT n.invitee FROM Notification n WHERE n.group.id = :groupId) AND u NOT IN (SELECT g.subscribers FROM Group g WHERE g.id = :groupId)")
    List<User> findAvailableUsersForInvitation(@Param("groupId") Long groupId, @Param("currentUserId") Long currentUserId, @Param("groupCreatorId") Long groupCreatorId);


    @Query("SELECT u FROM User u " +
            "WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT(:firstName, '%'))) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT(:lastName, '%'))) " +
            "AND (:patronymic IS NULL OR LOWER(u.patronymic) LIKE LOWER(CONCAT(:patronymic, '%')))")
    List<User> findUsersByFullName(@Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("patronymic") String patronymic);
}
