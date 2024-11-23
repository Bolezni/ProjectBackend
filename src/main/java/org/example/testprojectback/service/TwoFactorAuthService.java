package org.example.testprojectback.service;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.example.testprojectback.model.User;
import org.example.testprojectback.repository.UserRepository;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final UserRepository userRepository;

    public String generateSecretKey() {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        return new Base32().encodeToString(buffer);
    }

    public String findSecretByEmail(String email) {
       Optional<User>  user = userRepository.findByEmail(email);
        return user.map(User::getSecretKey).orElse(null);
    }

    public boolean verifyTotp(String secret, String userInputTotp) {
        Totp totpGenerator = new Totp(secret);
        String expectedTotp = totpGenerator.now();
        return expectedTotp.equals(userInputTotp);
    }
    public String generateQRUrl(String secret, String username) {
        String issuer = "WAIG";
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, username, new GoogleAuthenticatorKey.Builder(secret).build());
    }
}

