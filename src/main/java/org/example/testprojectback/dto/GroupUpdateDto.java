package org.example.testprojectback.dto;

import java.util.Set;

public record GroupUpdateDto(
        String chars,
        String name,
        String color,
        String description,
        Set<InterestDto> interests
) {
}
