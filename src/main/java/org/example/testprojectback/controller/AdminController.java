package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.GroupDto;
import org.example.testprojectback.dto.InterestDto;
import org.example.testprojectback.dto.UserDto;
import org.example.testprojectback.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;


    private static final String GET_ALL_USERS = "/users";
    private static final String GET_ALL_GROUPS = "/groups";
    private static final String GET_ALL_INTERESTS = "/interests";
    private static final String ADD_INTEREST = "/interest";
    private static final String DELETE_INTEREST_BY_NAME = "/interest/delete";
    private static final String DELETE_GROUP_BY_ID = "/group/{id}";
    private static final String DELETE_USER_BY_LOGIN = "/user/delete";


    @GetMapping(GET_ALL_USERS)
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping(GET_ALL_GROUPS)
    public List<GroupDto> getAllGroups() {
        return adminService.getAllGroups();
    }

    @GetMapping(GET_ALL_INTERESTS)
    public List<InterestDto> getAllInterests() {
        return adminService.getAllInterests();
    }

    @PostMapping(ADD_INTEREST)
    public InterestDto addInterest(@RequestParam String name,
                                   @RequestParam String color) {
        return adminService.addInterest(name,color);
    }

    @DeleteMapping(DELETE_GROUP_BY_ID)
    public ResponseEntity<?> deleteGroupById(@PathVariable Long id) {
        adminService.deleteGroupById(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DELETE_USER_BY_LOGIN)
    public ResponseEntity<?> deleteUserByUsername(@RequestParam(name = "login") String userName) {
        adminService.deleteUserByUserName(userName);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(DELETE_INTEREST_BY_NAME)
    public ResponseEntity<?> deleteInterestByName(@RequestParam(name = "interestName") String name) {
        adminService.deleteInterestByName(name);

        return ResponseEntity.ok().build();
    }
}
