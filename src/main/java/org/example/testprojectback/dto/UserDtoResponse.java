package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import java.time.LocalDate;

public record UserDtoResponse(
        String username,
        String firstname,
        String lastname,
        String patronymic,
        Gender gender,
        String email,
        String tgName,
        String profileImageId,
        String description,
        boolean isAdmin,
        Integer age,
        LocalDate birthDay,
        boolean isActivated
) {
}
