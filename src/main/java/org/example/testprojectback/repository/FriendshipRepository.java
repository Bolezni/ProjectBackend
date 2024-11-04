package org.example.testprojectback.repository;

import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUser (User user);
    Friendship findByUserAndFriend (User user, User friend);
}
