package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.FriendShipDto;
import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.FriendshipRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendShipService {

    private final FriendshipRepository friendshipRepository;

    private final NotificationService notificationService;

    private final UserRepository userRepository;


    @Transactional
    public List<User> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Friendship> friendships = friendshipRepository.findByUser(user);

        return friendships.stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }


    @Transactional
    public void sendFriendRequest(String userName, String friendName) {

        if(userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }

        if(userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }


        User user = userRepository
                .findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        User friend = userRepository
                .findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));


        Friendship friendship = friendshipRepository
                .findByUserAndFriend(user, friend);

        if (friendship == null) {
            friendship = Friendship.builder()
                    .user(user)
                    .friend(friend)
                    .status("PENDING")
                    .build();

            friendshipRepository.saveAndFlush(friendship);

        }
    }


    @Transactional
    public void acceptFriendRequest(String userName, String friendName) {

        if(userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if(userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);

        if (friendship != null && friendship.getStatus().equals("PENDING")) {
            friendship.setStatus("ACCEPTED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendshipRepository.saveAndFlush(friendship);
        }
    }

    @Transactional
    public void declineFriendRequest(String userName, String friendUserName) {

        if(userName.isEmpty() || friendUserName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }

        if(userName.equals(friendUserName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));


        User friend = userRepository.findByUsername(friendUserName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend);

        if (friendship != null && friendship.getStatus().equals("PENDING")) {
            friendship.setStatus("DECLINED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendshipRepository.saveAndFlush(friendship);
        }
    }

}
