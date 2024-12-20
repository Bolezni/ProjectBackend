package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String GET_USER_CREATED_GROUPS = "/api/user/{login}/groups/created";
    private static final String UPLOAD_PROFILE_IMAGE = "/api/user/{login}/profile-image";
    private static final String DELETE_INTEREST = "/api/user/{login}/interest/delete";
    private static final String DELETE_INTERESTS = "/api/user/{login}/interests";
    private static final String UPDATE_SECURITY_INFO = "/api/user/{login}/security";
    private static final String FETCH_USER_BY_FULLNAME = "/api/user/search_name";
    private static final String FETCH_USER_BY_INTERESTS = "/api/user/search_interest";
    private static final String FETCH_USER_BY_USERNAME = "/api/user/search_username";
    private static final String DELETE_USER_BY_LOGIN = "/api/user/{login}";
    private static final String GET_INTERESTS = "/api/user/{login}/interests";
    private static final String GET_USER_BY_LOGIN = "/api/user/{login}";
    private static final String GET_USER_GROUPS = "/api/user/{login}/groups";
    private static final String UPDATE_USER = "/api/user/{login}/update";
    private static final String ADD_INTERESTS_TO_USER = "/api/user/{login}/add/interests";
    private static final String UNSUBSCRIBE = "/api/user/{login}/unsubscribe";
    private static final String SUBSCRIBE = "/api/user/{login}/subscribe";


    @DeleteMapping(DELETE_USER_BY_LOGIN)
    public ResponseEntity<?> deleteUser(@PathVariable(name = "login") String userName) {
        userService.delete(userName);
        return ResponseEntity.ok().build();
    }

    @GetMapping(GET_INTERESTS)
    public Set<InterestDto> getInterests(@PathVariable(name = "login") String username) {
       return userService.getInterests(username);
    }
    @GetMapping(GET_USER_BY_LOGIN)
    public UserDtoResponse getUserByLogin(@PathVariable(name = "login") String userName) {
       return userService.getUserDtoByUserName(userName);
    }

    @GetMapping(GET_USER_GROUPS)
    public ResponseEntity<Page<GroupDto>> getUserSubscribedGroups(@PathVariable(name = "login") String userName,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<GroupDto> groups = userService.getUserSubscribedGroups(userName, page, size);
        return ResponseEntity.ok(groups);
    }

    @GetMapping(GET_USER_CREATED_GROUPS)
    public ResponseEntity<Page<GroupDto>> getUserCreatedGroups(
            @PathVariable(name = "login") String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GroupDto> groups = userService.getUserCreatedGroups(userName, page, size);
        return ResponseEntity.ok(groups);
    }

    @PutMapping(UPDATE_USER)
    public ResponseEntity<?> updateUser(@PathVariable(name = "login") String userName,
                                        @RequestBody UserDtoUpdate userDtoUpdate) {
        userService.updateUserProfile(userName,userDtoUpdate);

        return ResponseEntity.ok().build();
    }

    @PutMapping(UPDATE_SECURITY_INFO)
    public ResponseEntity<?> updateSecurityUserInfo(@PathVariable(name = "login") String username,
                                                    @Valid @RequestBody UserSecurityDataDto userSecurityDataDto){
        userService.updateSecurityUserData(username,userSecurityDataDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping(UPLOAD_PROFILE_IMAGE)
    public ResponseEntity<?> uploadProfileImage(@PathVariable(name = "login") String userName,
                                                @RequestParam String profileImageId){
        userService.uploadProfileImage(userName,profileImageId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DELETE_INTEREST)
    public ResponseEntity<?> removeInterestFromUser (@PathVariable(name = "login") String userName,
                                                     @RequestParam String interestName) {
        userService.removeInterestFromUser (userName, interestName);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DELETE_INTERESTS)
    public ResponseEntity<?> removeInterestsFromUser(@PathVariable(name = "login") String userName,
                                                     @RequestBody Set<InterestDto> interests){
        userService.removeInterestsFromUser(userName,interests);

        return ResponseEntity.ok().build();
    }

    @GetMapping(FETCH_USER_BY_FULLNAME)
    public List<UserDto> fetchUserByFullName(@RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestParam String patronymic) {
        return userService.fetchUserByFullName(firstName,lastName,patronymic);
    }

    @PostMapping(FETCH_USER_BY_FULLNAME)
    public List<UserDto> fetchUserByFullName(@RequestBody UserFetchDto userFetchDto) {
        return userService.fetchUserByFullName(userFetchDto);
    }

    @PostMapping(ADD_INTERESTS_TO_USER)
    public void addInterestsToUser(@PathVariable(name = "login") String username,
                                   @RequestBody Set<String> interests) {
        userService.addInterestsToUser(username, interests);
    }

    @PostMapping(FETCH_USER_BY_INTERESTS)
    public List<UserDto> fetchUserByInterest(@RequestBody Set<InterestDto> interests){
        return userService.fetchUserByInterest(interests);
    }

    @GetMapping(FETCH_USER_BY_USERNAME)
    public UserDto fetchUserByUserName(@RequestParam String username) {
        return userService.fetchUserByUserName(username);
    }

    @DeleteMapping(UNSUBSCRIBE)
    public ResponseEntity<?> unSubscribeGroup(@PathVariable(name = "login") String userName,
                                              @RequestParam Long groupId) {
        userService.unSubscribeGroup(userName,groupId);

        return ResponseEntity.ok().build();
    }
    @PostMapping(SUBSCRIBE)
    public ResponseEntity<?> subscribeToGroup(@PathVariable(name = "login") String username,
                                              @RequestParam Long groupId){
        userService.subscribeToGroup(username,groupId);

        return ResponseEntity.ok().build();
    }
}
