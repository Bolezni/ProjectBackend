package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.UserCredentialsDto;
import org.example.testprojectback.dto.UserRegisterDto;
import org.example.testprojectback.sercurity.RefreshTokenDto;
import org.example.testprojectback.sercurity.jwt.JwtAuthDto;
import org.example.testprojectback.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final UserService userService;

    private static final String SING_IN = "/sing-in";
    private static final String REGISTER = "/register";
    private static final String REFRESH = "/refresh";
    private static final String ACTIVATE_CODE = "/activate/{code}";

    @PostMapping(SING_IN)
    public ResponseEntity<JwtAuthDto> singIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        try {
            JwtAuthDto jwtAuthenticationDto = userService.singIn(userCredentialsDto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping(REGISTER)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDto userDto) {
        userService.addUser(userDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION)
                .build();
    }

    @PostMapping(REFRESH)
    public JwtAuthDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }

    @GetMapping(ACTIVATE_CODE)
    public ResponseEntity<?> activateUser(@PathVariable String code) {
        boolean isActivated =  userService.activateUser(code);
        if(isActivated) {
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
