package org.example.testprojectback.dto;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public record GroupCreateDto(
        @NotBlank(message = "Поле с сиволами не может быть пустым")
        String chars,
        @NotBlank(message = "Введите название группы")
        String name,
        String color,
        String description,
        UserDto creator,
        Set<InterestDto> interests,
        Set<UserDto> subscribers
) {
}
