package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import java.time.LocalDate;

public record UserDto(
        String username,
        String firstname,
        String lastname,
        String patronymic,
        String password,
        String email,
        Gender gender,
        String tgName,
        String description,
        String profileImageId,
        Integer age,
        boolean isAdmin,
        LocalDate birthDay
) {
}
