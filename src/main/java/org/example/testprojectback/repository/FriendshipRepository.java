package org.example.testprojectback.repository;

import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUser (User user);
    Optional<Friendship> findByUserAndFriend (User user, User friend);

    boolean existsByUserAndFriend (User user, User friend);
    boolean existsByFriendAndUser (User friend, User user);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friendship f WHERE f.user = :user AND f.friend = :friend AND f.status = 'PENDING'")
    boolean existsByUserAndFriendAndStatusPending(User user, User friend);

    void deleteByUserAndFriend(User user, User friend);

    List<Friendship> findByFriendAndStatus(User user, String pending);
}
