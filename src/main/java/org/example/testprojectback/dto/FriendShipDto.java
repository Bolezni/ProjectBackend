package org.example.testprojectback.dto;

import java.time.LocalDateTime;

public record FriendShipDto(
        UserDto user,
        UserDto friend,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
