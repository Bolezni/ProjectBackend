package org.example.testprojectback.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        UserDto inviter,
        UserDto invitee,
        GroupDto group,
        LocalDateTime createdAt
) {
}
