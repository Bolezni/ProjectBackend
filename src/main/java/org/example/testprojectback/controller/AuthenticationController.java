package org.example.testprojectback.controller;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.MfaVerificationRequest;
import org.example.testprojectback.dto.UserCredentialsDto;
import org.example.testprojectback.dto.UserRegisterDto;
import org.example.testprojectback.exceptions.UserNotFoundException;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.UserRepository;
import org.example.testprojectback.sercurity.RefreshTokenDto;
import org.example.testprojectback.sercurity.jwt.JwtAuthDto;
import org.example.testprojectback.service.TwoFactorAuthService;
import org.example.testprojectback.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;
    private final UserService userService;

    private static final String SING_IN = "/sing-in";
    private static final String REGISTER = "/register";
    private static final String REFRESH = "/refresh";
    private static final String ACTIVATE_CODE = "/activate/{code}";

    @PostMapping(SING_IN)
    public ResponseEntity<?> singIn(@RequestBody UserCredentialsDto userCredentialsDto) {
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


    @PostMapping("/generate-qr")
    public ResponseEntity<Map<String, String>> generateQRCode(@RequestParam String username) {
        Map<String, String> response = userService.linkQrCode(username);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify-totp")
    public ResponseEntity<?> verifyTotp(@Valid @RequestBody MfaVerificationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        String secret = twoFactorAuthService.findSecretByEmail(user.getEmail());
        boolean isValid = twoFactorAuthService.verifyTotp(secret, request.getTotp());

        if (isValid) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid TOTP code.");
        }
    }
}
