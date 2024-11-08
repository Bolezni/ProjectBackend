package org.example.testprojectback.repository;

import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUser (User user);
    Optional<Friendship> findByUserAndFriend (User user, User friend);

    boolean existsByUserAndFriend (User user, User friend);
    boolean existsByFriendAndUser (User friend, User user);
}
