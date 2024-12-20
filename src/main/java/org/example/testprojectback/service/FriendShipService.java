package org.example.testprojectback.service;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.FriendShipDto;
import org.example.testprojectback.dto.UserFriendDto;
import org.example.testprojectback.exceptions.FriendShipAlreadyExistsException;
import org.example.testprojectback.exceptions.FriendShipNotFoundException;
import org.example.testprojectback.exceptions.UserNotFoundException;
import org.example.testprojectback.mapper.FriendShipDtoMapper;
import org.example.testprojectback.mapper.UserFriendDtoMapper;
import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.FriendshipRepository;
import org.example.testprojectback.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserFriendDtoMapper userFriendDtoMapper;


    @Transactional
    public Page<UserFriendDto> getFriends(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Friendship> friendships = friendshipRepository.findByUserAndStatus(user, "ACCEPTED", pageable);

        return friendships.map(friendship -> userFriendDtoMapper.toDto(friendship.getFriend()));
    }



    @Transactional
    public FriendShipDto sendFriendRequest(String userName, String friendName) {

        if (userName == null || userName.isEmpty() || friendName == null || friendName.isEmpty()) {
            throw new RuntimeException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new RuntimeException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new UserNotFoundException("Friend not found"));

        if (friendshipRepository.existsByUserAndFriendAndStatusPending(user, friend) ||
                friendshipRepository.existsByUserAndFriendAndStatusPending(friend, user)) {
            throw new FriendShipAlreadyExistsException("Friend request already exists");
        }

        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status("PENDING")
                .updatedAt(LocalDateTime.now())
                .build();

        friendship = friendshipRepository.save(friendship);

        return friendShipDtoMapper.toDto(friendship);
    }


    @Transactional
    public FriendShipDto acceptFriendRequest(String userName, String friendName) {

        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new IllegalArgumentException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new IllegalArgumentException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new UserNotFoundException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new FriendShipNotFoundException("Friend request not found"));

        if(friendship.getStatus().equals("ACCEPTED")) {
            throw new IllegalArgumentException("No pending friend request");
        }
        if (friendship.getStatus().equals("PENDING")) {
            friendship.setStatus("ACCEPTED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendshipRepository.save(friendship);

            Friendship reverseFriendship = Friendship.builder()
                    .user(user)
                    .friend(friend)
                    .status("ACCEPTED")
                    .updatedAt(LocalDateTime.now())
                    .build();

            reverseFriendship = friendshipRepository.save(reverseFriendship);

            user.getFriends().add(friend);
            friend.getFriends().add(user);

            userRepository.save(user);
            userRepository.save(friend);

            return friendShipDtoMapper.toDto(reverseFriendship);
        } else {
            throw new IllegalArgumentException("Friend request is not in PENDING status");
        }
    }

    @Transactional
    public FriendShipDto declineFriendRequest(String userName, String friendName) {
        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new IllegalArgumentException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new IllegalArgumentException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new UserNotFoundException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new FriendShipNotFoundException("Friend request not found"));

        if ("PENDING".equals(friendship.getStatus())) {
            friendship.setStatus("DECLINED");
            friendship.setUpdatedAt(LocalDateTime.now());
            friendship = friendshipRepository.save(friendship);

            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            friendshipRepository.delete(friendship);

            return friendShipDtoMapper.toDto(friendship);
        } else {
            throw new IllegalArgumentException("Friend request is not in PENDING status");
        }
    }


    @Transactional
    public void removeFriend(String userName, String friendName) {
        if (userName.isEmpty() || friendName.isEmpty()) {
            throw new IllegalArgumentException("Username or Friend name cannot be empty");
        }
        if (userName.equals(friendName)) {
            throw new IllegalArgumentException("Username and Friend name cannot be the same");
        }

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByUsername(friendName)
                .orElseThrow(() -> new UserNotFoundException("Friend not found"));

        if (!user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Friend not found in user's friend list");
        }


        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        userRepository.save(user);
        userRepository.save(friend);

        friendshipRepository.deleteByUserAndFriend(user, friend);
        friendshipRepository.deleteByUserAndFriend(friend, user);
    }

    public List<FriendShipDto> getIncomingFriendRequests(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        List<Friendship> incomingRequests = friendshipRepository.findByFriendAndStatus(user, "PENDING");

        return incomingRequests.stream()
                .map(friendShipDtoMapper::toDto)
                .collect(Collectors.toList());
    }


    public boolean areFriends(String userName1, String userName2) {

        User user1 = userRepository.findByUsername(userName1)
                .orElseThrow(() -> new UserNotFoundException("User  1 not found"));

        User user2 = userRepository.findByUsername(userName2)
                .orElseThrow(() -> new UserNotFoundException("User  2 not found"));

        return friendshipRepository.existsByUserAndFriend(user1, user2) &&
        friendshipRepository.existsByFriendAndUser(user2, user1);
    }
}
