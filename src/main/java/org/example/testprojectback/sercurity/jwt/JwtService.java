package org.example.testprojectback.sercurity.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
public class JwtService {

    @Value("33fe8a86b72e57ff32adffc8790cdf26e27045a350c29c4139fa1c59c549d3cf5515c3d7d2d200f61d567597f07ae6a96e40dbc5cc2878b9aa414a9b33fc399f")
    private String jwtSecret;

    public JwtAuthDto generateAuthToken(String username){
        JwtAuthDto jwtAuthDto = new JwtAuthDto();
        jwtAuthDto.setToken(generateJwtToken(username));
        jwtAuthDto.setRefreshToken(generateRefreshToken(username));
        return jwtAuthDto;
    }

    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public JwtAuthDto refreshBaseToken(String username, String refreshToken) {
        JwtAuthDto jwtDto = new JwtAuthDto();
        jwtDto.setToken(generateJwtToken(username));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        }catch (ExpiredJwtException expEx){
            log.error("Expired JwtException", expEx);
        }catch (UnsupportedJwtException expEx){
            log.error("Unsupported JwtException", expEx);
        }catch (MalformedJwtException expEx){
            log.error("Malformed JwtException", expEx);
        }catch (SecurityException expEx){
            log.error("Security Exception", expEx);
        }catch (Exception expEx){
            log.error("invalid token", expEx);
        }
        return false;
    }

    private String generateJwtToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private String generateRefreshToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
