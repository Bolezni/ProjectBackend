package org.example.testprojectback.dto;

import org.example.testprojectback.model.Gender;

import java.time.LocalDate;
import java.util.Set;

public record UserDtoUpdate (
        String firstname,
        String lastname,
        String patronymic,
        Gender gender,
        String tgName,
        String description,
        LocalDate birthDay,
        Set<InterestDto> interests
){
}
