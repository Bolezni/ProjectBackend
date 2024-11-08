package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.FriendShipDto;
import org.example.testprojectback.mapper.FriendShipDtoMapper;
import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.FriendshipRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendShipService {

    private final FriendshipRepository friendshipRepository;

    private final UserRepository userRepository;
    private final FriendShipDtoMapper friendShipDtoMapper;


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
    public FriendShipDto sendFriendRequest(String userName, String friendName) {

        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (friendshipRepository.existsByUserAndFriend(user, friend)) {
            throw new RuntimeException("Friend request already exists");
        }

        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status("PENDING")
                .build();

        friendship = friendshipRepository.save(friendship);
        return friendShipDtoMapper.toDto(friendship);
    }


    @Transactional
    public FriendShipDto acceptFriendRequest(String userName, String friendName) {

        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (friendship.getStatus().equals("PENDING")) {
            friendship.setStatus("ACCEPTED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendshipRepository.save(friendship);

            Friendship reverseFriendship =  Friendship.builder()
                    .user(user)
                    .friend(friend)
                    .status("ACCEPTED")
                    .build();

            reverseFriendship = friendshipRepository.save(reverseFriendship);
            return friendShipDtoMapper.toDto(reverseFriendship);
        } else {
            throw new RuntimeException("Friend request is not in PENDING status");
        }
    }

    @Transactional
    public FriendShipDto declineFriendRequest(String userName, String friendName) {
        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (friendship.getStatus().equals("PENDING")) {
            friendship.setStatus("DECLINED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendship = friendshipRepository.save(friendship);

            return friendShipDtoMapper.toDto(friendship);
        }else {
            throw new RuntimeException("Friend request is not in PENDING status");
        }
    }


    @Transactional
    public void removeFriend(String userName, String friendName) {
        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        friendshipRepository.delete(friendship);

        Friendship reverseFriendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new RuntimeException("Reverse friendship not found"));

        friendshipRepository.delete(reverseFriendship);
    }


    public boolean areFriends(String userName1, String userName2) {

        User user1 = userRepository.findByUsername(userName1)
                .orElseThrow(() -> new RuntimeException("User  1 not found"));

        User user2 = userRepository.findByUsername(userName2)
                .orElseThrow(() -> new RuntimeException("User  2 not found"));

        return friendshipRepository.existsByUserAndFriend(user1, user2) ||
        friendshipRepository.existsByFriendAndUser(user2, user1);
    }
}
