package org.example.projectback.api.dto;

import org.example.projectback.store.entity.enums.Gender;

import java.time.LocalDate;

public record UserRegistrationRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String tgName,
        LocalDate birthDate,
        Gender gender
) {
}
