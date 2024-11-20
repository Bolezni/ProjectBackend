package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.FriendShipDto;
import org.example.testprojectback.dto.UserFriendDto;
import org.example.testprojectback.service.FriendShipService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendShipController {

    private final FriendShipService friendShipService;

    private static final String ACCEPT_FRIEND = "/accept";
    private static final String DECLINE_FRIEND = "/decline";
    private static final String GET_FRIENDS = "/search";
    private static final String REQUEST_FRIEND = "/send";
    private static final String REMOVE = "/remove";
    private static final String IS_FRIEND = "/isFriend";
    private static final String INCOMING_FRIEND = "/incoming-requests";


    @PostMapping(REQUEST_FRIEND)
    public FriendShipDto sendFriendRequest(@RequestParam(name = "login") String userName,
                                           @RequestParam(name = "friendLogin") String friendName){
        return friendShipService.sendFriendRequest(userName, friendName);
    }


    @PostMapping(ACCEPT_FRIEND)
    public FriendShipDto acceptFriendRequest(@RequestParam(name = "login") String userName,
                                             @RequestParam(name = "friendLogin") String friendName) {
        return friendShipService.acceptFriendRequest(userName, friendName);
    }

    @PostMapping(DECLINE_FRIEND)
    public FriendShipDto declineFriendRequest(@RequestParam(name = "login") String userName,
                                              @RequestParam(name = "friendLogin") String friendName) {
        return friendShipService.declineFriendRequest(userName, friendName);
    }

    @GetMapping(GET_FRIENDS)
    public ResponseEntity<Page<UserFriendDto>> getFriends(
            @RequestParam(name = "login") String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserFriendDto> friends = friendShipService.getFriends(userName, page, size);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping(REMOVE)
    public ResponseEntity<?> removeFriend(@RequestParam String userName,
                                               @RequestParam String friendName) {
        friendShipService.removeFriend(userName, friendName);
        return ResponseEntity.ok().body("Remove friend successfully");
    }

    @GetMapping(IS_FRIEND)
    public ResponseEntity<?> areFriend(@RequestParam String userName,
                                       @RequestParam String friendName) {
        boolean isFriend = friendShipService.areFriends(userName,friendName);
        if(isFriend){
            return ResponseEntity.ok().body("Friend are accepted.");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend is not accepted.");
        }
    }

    @GetMapping(INCOMING_FRIEND)
    public ResponseEntity<List<FriendShipDto>> getIncomingFriendRequests(@RequestParam(name = "login") String userName) {
        List<FriendShipDto> requests = friendShipService.getIncomingFriendRequests(userName);
        return ResponseEntity.ok(requests);
    }


}
