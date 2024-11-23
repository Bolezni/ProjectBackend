package org.example.testprojectback.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MfaVerificationRequest {
    @NotBlank(message = "Login cant be empty")
    private String username;

    @NotBlank(message = "TOTP code must not be blank")
    private String totp;
}
