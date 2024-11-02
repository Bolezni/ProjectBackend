package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.UserDtoUpdate;
import org.example.testprojectback.mapper.UserDtoUpdatesMapper;
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

    private static final String ACCEPT_FRIEND = "/{login}/accept";
    private static final String DECLINE_FRIEND = "/{login}/decline";
    private static final String GET_FRIENDS = "/search";
    private static final String REQUEST_FRIEND = "/{login}/request";


    @PostMapping(REQUEST_FRIEND)
    public ResponseEntity<?> sendFriendRequest(@PathVariable(name = "login") String userName,
                                               @RequestParam(name = "friendLogin") String friendLogin){
        friendShipService.sendFriendRequest(userName, friendLogin);

        return ResponseEntity.ok().build();
    }


    @PostMapping(ACCEPT_FRIEND)
    public ResponseEntity<?> acceptFriendRequest(@PathVariable(name = "login") String userName,
                                          @RequestParam(name = "friendLogin") String friendLogin) {
        friendShipService.acceptFriendRequest(userName, friendLogin);
        return ResponseEntity.ok().build();
    }

    @PostMapping(DECLINE_FRIEND)
    public ResponseEntity<?> declineFriendRequest(@PathVariable(name = "login") String userName,
                                          @RequestParam(name = "friendLogin") String friendLogin) {
        friendShipService.declineFriendRequest(userName, friendLogin);
        return ResponseEntity.ok().build();
    }

    @GetMapping(GET_FRIENDS)
    public List<UserDtoUpdate> getFriends(@RequestParam(name = "login") String userName) {
        return friendShipService.getFriends(userName).
                stream()
                .map(userDtoUpdatesMapper::toDto)
                .collect(Collectors.toList());
    }


}
