package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupDto;
import org.example.testprojectback.dto.GroupUpdateDto;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.dto.NotificationDto;
import org.example.testprojectback.model.User;
import org.example.testprojectback.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private static final String CREATE_GROUP = "/groups/{login}/create";
    private static final String ADD_INTEREST_TO_GROUP = "/groups/{groupId}/interests";
    private static final String INVITE_USER_TO_GROUP = "/groups/{groupId}/users";
    private static final String ADD_USERS_TO_GROUPS = "/groups/{groupId}/users/add";
    private static final String FETCH_GROUP_BY_PREFIX_NAME = "/groups/search_name";
    private static final String FETCH_GROUPS_BY_INTEREST = "/groups/search_interest";
    private static final String DELETE_GROUP_BY_ID = "/groups/{groupId}";
    private static final String ACCEPT_INVATION = "/groups/notifications/accept";
    private static final String CANCEL_INVATION = "/groups/notifications/cancel";


    @GetMapping("/groups")
    public List<GroupDto> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PostMapping(CREATE_GROUP)
    public ResponseEntity<?> createGroup(@PathVariable(name = "login") String userName,
                                         @RequestBody GroupDto group) {
         groupService.createGroup(userName,group);

         return ResponseEntity.ok()
                 .build();
    }

//    @PostMapping("/groups/{groupId}/add")
//    public ResponseEntity<?> addUserToGroup(@PathVariable Long groupId,
//                                            @RequestParam(name = "login") String userName) {
//        groupService.addUserToGroup(groupId,userName);
//
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/groups/{groupID}")
    public GroupDto getGroup(@PathVariable(name = "groupID") Long groupID) {
        return groupService.getGroupById(groupID);
    }

    @PostMapping(ADD_INTEREST_TO_GROUP)
    public void addInterestToGroup(@PathVariable Long groupId,
                                   @RequestBody Set<String> interests) {
        groupService.addInterestToGroup(groupId, interests);
    }

    @PostMapping(INVITE_USER_TO_GROUP)
    public NotificationDto inviteUserToGroup(@PathVariable Long groupId,
                                             @RequestParam(name = "login") String userName) {
        return groupService.inviteUserToGroup(groupId, userName);
    }

    @PostMapping(ACCEPT_INVATION)
    public ResponseEntity<?> acceptInvitation(@RequestParam Long notificationId) {
        groupService.acceptGroupInvitation(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(CANCEL_INVATION)
    public ResponseEntity<?> cancelInvitation(@RequestParam Long notificationId) {
        groupService.cancelGroupInvitation(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ADD_USERS_TO_GROUPS)
    public void addUsersToGroup(@PathVariable Long groupId,
                                @RequestBody Set<User> users){
        groupService.addUsersToGroup(groupId,users);
    }

    @GetMapping(FETCH_GROUP_BY_PREFIX_NAME)
    public List<GroupDto> fetchGroupsByName(@RequestParam(value = "prefix_name",required = false)
                                            Optional<String> optionalPrefixName) {
       return groupService.fetchGroupsByName(optionalPrefixName);
    }

    @GetMapping(FETCH_GROUPS_BY_INTEREST)
    public List<GroupDto> fetchGroupsByInterest(@RequestBody Set<InterestDto> interests) {
        return groupService.fetchGroupsByInterest(interests);
    }

    @DeleteMapping(DELETE_GROUP_BY_ID)
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/groups/{groupId}/update")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId,
                                         @RequestBody GroupUpdateDto groupUpdateDto){
        groupService.updateGroup(groupId,groupUpdateDto);

        return ResponseEntity.ok().build();
    }

}
