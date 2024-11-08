package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.mapper.GroupDtoMapper;
import org.example.testprojectback.model.Group;
import org.example.testprojectback.model.User;
import org.example.testprojectback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GroupDtoMapper groupDtoMapper;

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
        return ResponseEntity
                .ok(HttpStatus.ACCEPTED);
    }

    @GetMapping(GET_INTERESTS)
    public Set<InterestDto> getInterests(@PathVariable(name = "login") String userName) {
       return userService.getInterests(userName);
    }
    @GetMapping(GET_USER_BY_LOGIN)
    public UserDtoResponse getUserByLogin(@PathVariable(name = "login") String userName) {
       return userService.getUserDtoByUserName(userName);
    }

    @GetMapping(GET_USER_GROUPS)
    public Set<GroupDto> getUserSubscribedGroups(@PathVariable(name = "login") String userName) {
        return userService.getUserSubscribedGroups(userName);
    }

    @GetMapping(GET_USER_CREATED_GROUPS)
    public Set<GroupDto> getUserCreatedGroups(@PathVariable(name = "login") String userName) {
        Set<Group> groups = userService.getUserByUserName(userName)
                .map(User::getCreatedGroups)
                .orElseThrow(() -> new RuntimeException(
                        "User not found"
                ));

        return groups.stream()
                .map(groupDtoMapper::toDto)
                .collect(Collectors.toSet());
    }

    @PutMapping(UPDATE_USER)
    public ResponseEntity<?> updateUser(@PathVariable(name = "login") String userName,
                                        @RequestBody UserDtoUpdate userDtoUpdate) {
        userService.updateUserProfile(userName,userDtoUpdate);

        return ResponseEntity.ok()
                .body(HttpStatus.ACCEPTED);
    }

    @PutMapping(UPDATE_SECURITY_INFO)
    public ResponseEntity<?> updateSecurityUserInfo(@PathVariable(name = "login") String userName,
                                                    @RequestParam(name = "new_login") String newUserName,
                                                    @RequestParam(name = "password") String newPassword,
                                                    @RequestParam(name = "email") String newEmail){
        userService.updateSecurityUserData(userName,newUserName,newPassword,newEmail);

        return ResponseEntity.ok()
                .body(HttpStatus.ACCEPTED);
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

        return ResponseEntity.ok()
                .body(HttpStatus.ACCEPTED);
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

    @PostMapping(ADD_INTERESTS_TO_USER)
    public void addInterestsToUser(@PathVariable(name = "login") String username,
                                   @RequestBody Set<String> interests) {
        userService.addInterestsToUser(username, interests);
    }

    @GetMapping(FETCH_USER_BY_INTERESTS)
    public List<UserDto> fetchUserByInterest(@RequestBody Set<InterestDto> interests){
        return userService.fetchUserByInterest(interests);
    }

    @GetMapping(FETCH_USER_BY_USERNAME)
    public UserDto fetchUserByUserName(@RequestBody String username) {
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
