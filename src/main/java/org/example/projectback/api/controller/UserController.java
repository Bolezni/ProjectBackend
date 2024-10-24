package org.example.projectback.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectback.api.dto.AckDto;
import org.example.projectback.api.dto.UserDto;
import org.example.projectback.api.dto.UserRegistrationRequest;
import org.example.projectback.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String CREATE_USER = "/api/user/add";
    private static final String GET_USER = "/api/user/";
    private static final String UPDATE_USER = "/api/user/{id}";
    private static final String DELETE_USER = "/api/user/{id}";


    @PostMapping(CREATE_USER)
    public UserDto registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest){
        return userService.createUser(userRegistrationRequest);
    }

    @GetMapping(GET_USER)
    public UserDto getUserByLogin(@RequestParam(name = "login") String login){
        return userService.getByUserName(login);
    }

    @PutMapping(UPDATE_USER)
    public UserDto updateUser(@RequestParam(name = "id") UUID id, @RequestBody UserDto userDto){

        return null;
    }

    @DeleteMapping(DELETE_USER)
    public AckDto deleteUser(@PathVariable(name = "id") UUID id){
        return userService.delete(id);
    }

}
