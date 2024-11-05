package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.UserDtoUpdate;
import org.example.testprojectback.dto.UserFriendDto;
import org.example.testprojectback.mapper.UserDtoUpdatesMapper;
import org.example.testprojectback.mapper.UserFriendDtoMapper;
import org.example.testprojectback.model.Friendship;
import org.example.testprojectback.service.FriendShipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendShipController {


    private final FriendShipService friendShipService;
    private final UserDtoUpdatesMapper userDtoUpdatesMapper;

    private static final String ACCEPT_FRIEND = "/accept";
    private static final String DECLINE_FRIEND = "/decline";
    private static final String GET_FRIENDS = "/search";
    private static final String REQUEST_FRIEND = "/send";
    private static final String REMOVE = "/remove";
    private final UserFriendDtoMapper userFriendDtoMapper;

    @PostMapping(REQUEST_FRIEND)
    public ResponseEntity<Friendship> sendFriendRequest(@RequestParam(name = "login") String userName,
                                               @RequestParam(name = "friendLogin") String friendName){
        friendShipService.sendFriendRequest(userName, friendName);

        return ResponseEntity.ok().build();
    }


    @PostMapping(ACCEPT_FRIEND)
    public ResponseEntity<Friendship> acceptFriendRequest(@RequestParam(name = "login") String userName,
                                                          @RequestParam(name = "friendLogin") String friendName) {
        friendShipService.acceptFriendRequest(userName, friendName);
        return ResponseEntity.ok().build();
    }

    @PostMapping(DECLINE_FRIEND)
    public ResponseEntity<Friendship> declineFriendRequest(@RequestParam(name = "login") String userName,
                                          @RequestParam(name = "friendLogin") String friendName) {
        friendShipService.declineFriendRequest(userName, friendName);
        return ResponseEntity.ok().build();
    }

    @GetMapping(GET_FRIENDS)
    public List<UserFriendDto> getFriends(@RequestParam(name = "login") String userName) {
        return friendShipService.getFriends(userName).
                stream()
                .map(userFriendDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping(REMOVE)
    public ResponseEntity<String> removeFriend(@RequestParam String userName, @RequestParam String friendName) {
        friendShipService.removeFriend(userName, friendName);
        return ResponseEntity.ok("Friend removed successfully.");
    }


}
