package org.example.testprojectback.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        UserDto user,
        GroupDto group,
        String message,
        LocalDateTime createdAt
) {
}
