package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import java.time.LocalDate;

public record UserDto(
        String username,
        String firstname,
        String lastname,
        String patronymic,
        String email,
        Gender gender,
        String tgName,
        String description,
        String profileImageId,
        Integer age,
        boolean activeCode,
        boolean isAdmin,
        LocalDate birthDay
) {
}
