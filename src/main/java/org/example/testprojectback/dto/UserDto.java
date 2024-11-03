package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import java.time.LocalDate;

public record UserDto(
        String userName,
        String firstName,
        String lastName,
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
