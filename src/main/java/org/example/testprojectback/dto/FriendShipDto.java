package org.example.testprojectback.dto;

import java.time.LocalDateTime;

public record FriendShipDto(
        Long id,
        UserDto user,
        UserDto friend,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
