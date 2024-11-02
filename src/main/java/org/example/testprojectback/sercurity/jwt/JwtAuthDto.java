package org.example.testprojectback.sercurity.jwt;

import lombok.Data;

@Data
public class JwtAuthDto {

    private String token;
    private String refreshToken;
}
