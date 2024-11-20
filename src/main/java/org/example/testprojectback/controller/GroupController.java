package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.*;
import org.example.testprojectback.service.GroupService;
import org.example.testprojectback.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final NotificationService notificationService;

    private static final String CREATE_GROUP = "/groups/{login}/create";
    private static final String ADD_INTEREST_TO_GROUP = "/groups/{groupId}/interests";
    private static final String INVITE_USER_TO_GROUP = "/groups/{groupId}/users";
    private static final String FETCH_GROUP_BY_PREFIX_NAME = "/groups/search_name";
    private static final String FETCH_GROUPS_BY_INTEREST = "/groups/search_interest";
    private static final String DELETE_GROUP_BY_ID = "/groups/{groupId}";
    private static final String ACCEPT_INVATION = "/groups/notifications/accept";
    private static final String CANCEL_INVATION = "/groups/notifications/cancel";
    private static final String UPDATE_GROUP = "/groups/{groupId}/update";
    private static final String GET_ALL_GROUPS = "/groups";
    private static final String GET_GROUP_BY_ID = "/groups/{groupID}";
    private static final String TOP_INTEREST = "/groups/top-by-interest";


    @GetMapping(GET_ALL_GROUPS)
    public ResponseEntity<Page<GroupDto>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GroupDto> groups = groupService.getAllGroups(page, size);
        return ResponseEntity.ok(groups);
    }



    @PostMapping(CREATE_GROUP)
    public ResponseEntity<?> createGroup(@PathVariable(name = "login") String userName,
                                         @Valid @RequestBody GroupCreateDto group) {
         groupService.createGroup(userName,group);

         return ResponseEntity.ok()
                 .build();
    }

    @GetMapping(GET_GROUP_BY_ID)
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
                                             @RequestParam(name = "from_username") String fromUserName,
                                             @RequestParam(name = "to_username") String toUserName) {
        return notificationService.createNotification(fromUserName, toUserName, groupId);
    }

    @PostMapping(ACCEPT_INVATION)
    public ResponseEntity<?> acceptInvitation(@RequestParam Long notificationId) {
        notificationService.acceptNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(CANCEL_INVATION)
    public ResponseEntity<?> cancelInvitation(@RequestParam Long notificationId) {
        notificationService.cancelNotification(notificationId);
        return ResponseEntity.ok().build();
    }


    @GetMapping(FETCH_GROUP_BY_PREFIX_NAME)
    public List<GroupDto> fetchGroupsByName(@RequestParam(value = "prefix_name",required = false)
                                            Optional<String> optionalPrefixName) {
       return groupService.fetchGroupsByName(optionalPrefixName);
    }

    @PostMapping(FETCH_GROUPS_BY_INTEREST)
    public List<GroupDto> fetchGroupsByInterest(@RequestBody Set<InterestDto> interests) {
        return groupService.fetchGroupsByInterest(interests);
    }

    @DeleteMapping(DELETE_GROUP_BY_ID)
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);

        return ResponseEntity.ok().build();
    }

    @PutMapping(UPDATE_GROUP)
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId,
                                         @RequestBody GroupUpdateDto groupUpdateDto){
        groupService.updateGroup(groupId,groupUpdateDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping(TOP_INTEREST)
    public ResponseEntity<Map<String, List<GroupDto>>> getTopGroupsByInterest(
            @RequestParam(defaultValue = "6") int maxGroupsPerInterest) {
        Map<String, List<GroupDto>> result = groupService.getTopGroupsGroupedByInterest(maxGroupsPerInterest);
        return ResponseEntity.ok(result);
    }

}
