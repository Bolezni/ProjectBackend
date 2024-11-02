package org.example.testprojectback.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        UserDto user,
        GroupDto group,
        String message,
        LocalDateTime createdAt
) {
}
