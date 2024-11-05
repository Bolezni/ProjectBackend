package org.example.testprojectback.dto;

public record UserFriendDto(
        String username,
        String firstname,
        String lastname,
        String profileImageId
) {
}
