package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

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
        Integer age
) {
}
