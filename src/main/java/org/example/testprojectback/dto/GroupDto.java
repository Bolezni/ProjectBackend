package org.example.testprojectback.dto;

import java.util.Set;

public record GroupDto(
        Long id,
        String chars,
        String name,
        String color,
        String description,
        UserDto creator,
        Set<InterestDto> interests,
        Set<UserDto> subscribers
) {
}
