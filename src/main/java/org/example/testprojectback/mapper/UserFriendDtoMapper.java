package org.example.testprojectback.mapper;

import lombok.RequiredArgsConstructor;
import org.example.testprojectback.dto.UserFriendDto;
import org.example.testprojectback.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFriendDtoMapper {

    public UserFriendDto toDto(User user) {
        return new UserFriendDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageId()
        );
    }

    public User toEntity(UserFriendDto dto) {
        return User.builder()
                .username(dto.username())
                .firstName(dto.firstname())
                .lastName(dto.lastname())
                .profileImageId(dto.profileImageId())
                .build();
    }
}
